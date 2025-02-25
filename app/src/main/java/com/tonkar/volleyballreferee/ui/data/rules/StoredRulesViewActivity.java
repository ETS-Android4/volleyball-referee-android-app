package com.tonkar.volleyballreferee.ui.data.rules;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.tonkar.volleyballreferee.R;
import com.tonkar.volleyballreferee.engine.Tags;
import com.tonkar.volleyballreferee.engine.api.JsonConverters;
import com.tonkar.volleyballreferee.engine.api.model.ApiRules;
import com.tonkar.volleyballreferee.engine.rules.Rules;
import com.tonkar.volleyballreferee.engine.service.StoredRulesManager;
import com.tonkar.volleyballreferee.engine.service.StoredRulesService;
import com.tonkar.volleyballreferee.ui.interfaces.RulesHandler;
import com.tonkar.volleyballreferee.ui.util.UiUtils;

public class StoredRulesViewActivity extends AppCompatActivity {

    private StoredRulesService mStoredRulesService;
    private Rules              mRules;

    public StoredRulesViewActivity() {
        super();
        getSupportFragmentManager().addFragmentOnAttachListener((fragmentManager, fragment) -> {
            if (fragment instanceof RulesHandler) {
                RulesHandler rulesHandler = (RulesHandler) fragment;
                rulesHandler.setRules(mRules);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mStoredRulesService = new StoredRulesManager(this);
        mRules = new Rules();
        mRules.setAll(JsonConverters.GSON.fromJson(getIntent().getStringExtra("rules"), ApiRules.class));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stored_rules_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView nameText = findViewById(R.id.stored_rules_name);
        ImageView kindItem = findViewById(R.id.rules_kind_item);

        nameText.setText(mRules.getName());

        switch (mRules.getKind()) {
            case INDOOR_4X4:
                UiUtils.colorChipIcon(this, R.color.colorIndoor4x4Light, R.drawable.ic_4x4_small, kindItem);
                break;
            case BEACH:
                UiUtils.colorChipIcon(this, R.color.colorBeachLight, R.drawable.ic_beach, kindItem);
                break;
            case SNOW:
                UiUtils.colorChipIcon(this, R.color.colorSnowLight, R.drawable.ic_snow, kindItem);
                break;
            case INDOOR:
            default:
                UiUtils.colorChipIcon(this, R.color.colorIndoorLight, R.drawable.ic_6x6_small, kindItem);
                break;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, RulesFragment.newInstance());
        fragmentTransaction.commit();

        ScrollView scrollView = findViewById(R.id.rules_scroll_view);
        ExtendedFloatingActionButton editRulesButton = findViewById(R.id.edit_rules_button);
        UiUtils.addExtendShrinkListener(scrollView, editRulesButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_stored_rules_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                backToList();
                return true;
            case R.id.action_delete_rules:
                deleteRules();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        backToList();
    }

    public void editRules(View view) {
        ApiRules rules = mStoredRulesService.getRules(mRules.getId());
        Log.i(Tags.STORED_RULES, String.format("Start activity to edit stored rules %s", rules.getName()));

        final Intent intent = new Intent(this, StoredRulesActivity.class);
        intent.putExtra("rules", JsonConverters.GSON.toJson(rules, ApiRules.class));
        intent.putExtra("create", false);
        startActivity(intent);
        UiUtils.animateForward(this);
    }

    private void backToList() {
        Intent intent = new Intent(this, StoredRulesListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        UiUtils.animateBackward(this);
    }

    private void deleteRules() {
        Log.i(Tags.STORED_RULES, "Delete rules");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dialog);
        builder.setTitle(getString(R.string.delete_rules)).setMessage(getString(R.string.delete_rules_question));
        builder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
            StoredRulesService storedRulesService = new StoredRulesManager(this);
            storedRulesService.deleteRules(mRules.getId());
            UiUtils.makeText(this, getString(R.string.deleted_rules), Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, StoredRulesListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            UiUtils.animateBackward(this);
        });
        builder.setNegativeButton(android.R.string.no, (dialog, which) -> {});
        AlertDialog alertDialog = builder.show();
        UiUtils.setAlertDialogMessageSize(alertDialog, getResources());
    }
}
