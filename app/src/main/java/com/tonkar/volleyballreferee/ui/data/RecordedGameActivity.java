package com.tonkar.volleyballreferee.ui.data;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.tonkar.volleyballreferee.R;
import com.tonkar.volleyballreferee.business.PrefUtils;
import com.tonkar.volleyballreferee.business.data.ScoreSheetWriter;
import com.tonkar.volleyballreferee.interfaces.Tags;
import com.tonkar.volleyballreferee.interfaces.data.StoredGamesService;
import com.tonkar.volleyballreferee.interfaces.data.StoredGameService;
import com.tonkar.volleyballreferee.interfaces.team.TeamType;
import com.tonkar.volleyballreferee.ui.interfaces.StoredGameServiceHandler;
import com.tonkar.volleyballreferee.ui.util.UiUtils;

import java.io.File;

public abstract class RecordedGameActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    protected StoredGamesService mStoredGamesService;
    protected long               mGameDate;
    protected StoredGameService  mStoredGameService;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_recorded_game, menu);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            MenuItem shareMenu = menu.findItem(R.id.action_share_game);
            shareMenu.setVisible(false);

            MenuItem scoreSheetMenu = menu.findItem(R.id.action_generate_score_sheet);
            scoreSheetMenu.setVisible(false);
        }

        MenuItem recordMenu = menu.findItem(R.id.action_index_game);

        if (PrefUtils.isSyncOn(this)) {
            if (mStoredGamesService.isGameIndexed(mGameDate)) {
                recordMenu.setIcon(R.drawable.ic_public_menu);
            } else {
                recordMenu.setIcon(R.drawable.ic_private_menu);
            }
        } else {
            recordMenu.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_index_game:
                toggleGameIndexed();
                return true;
            case R.id.action_generate_score_sheet:
                generateScoreSheet();
                return true;
            case R.id.action_delete_game:
                deleteGame();
                return true;
            case R.id.action_share_game:
                shareGame();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toggleGameIndexed() {
        Log.i(Tags.STORED_GAMES, "Toggle game indexed");
        if (PrefUtils.isSyncOn(this)) {
            mStoredGamesService.toggleGameIndexed(mGameDate);
            invalidateOptionsMenu();
        }
    }

    private void generateScoreSheet() {
        Log.i(Tags.STORED_GAMES, "Generate score sheet");
        File file = ScoreSheetWriter.writeRecordedGame(this, mStoredGamesService.getGame(mGameDate));
        if (file == null) {
            UiUtils.makeText(this, getResources().getString(R.string.report_exception), Toast.LENGTH_LONG).show();
        } else {
            Uri uri = FileProvider.getUriForFile(this, "com.tonkar.volleyballreferee.fileprovider", file);
            grantUriPermission("com.tonkar.volleyballreferee.fileprovider", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(uri,"text/html");
            target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent intent = Intent.createChooser(target, file.getName());
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e(Tags.STORED_GAMES, "Exception while showing report", e);
                UiUtils.makeText(this, getResources().getString(R.string.report_exception), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void deleteGame() {
        Log.i(Tags.STORED_GAMES, "Delete game");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dialog);
        builder.setTitle(getResources().getString(R.string.delete_game)).setMessage(getResources().getString(R.string.delete_game_question));
        builder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
            mStoredGamesService.deleteGame(mGameDate);
            UiUtils.makeText(RecordedGameActivity.this, getResources().getString(R.string.deleted_game), Toast.LENGTH_LONG).show();

            Intent intent = new Intent(RecordedGameActivity.this, RecordedGamesListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            UiUtils.animateBackward(this);
        });
        builder.setNegativeButton(android.R.string.no, (dialog, which) -> {});
        AlertDialog alertDialog = builder.show();
        UiUtils.setAlertDialogMessageSize(alertDialog, getResources());
    }

    private void shareGame() {
        Log.i(Tags.STORED_GAMES, "Share game");
        UiUtils.shareRecordedGame(this, mStoredGamesService.getGame(mGameDate));
    }

    protected String buildScore(StoredGameService storedGameService) {
        StringBuilder builder = new StringBuilder();
        builder.append("\t\t");
        for (int setIndex = 0; setIndex < storedGameService.getNumberOfSets(); setIndex++) {
            int homePoints = storedGameService.getPoints(TeamType.HOME, setIndex);
            int guestPoints = storedGameService.getPoints(TeamType.GUEST, setIndex);
            builder.append(UiUtils.formatScoreFromLocale(homePoints, guestPoints, false)).append("\t\t");
        }

        return builder.toString();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof StoredGameServiceHandler) {
            StoredGameServiceHandler storedGameServiceHandler = (StoredGameServiceHandler) fragment;
            storedGameServiceHandler.setStoredGameService(mStoredGameService);
        }
    }
}
