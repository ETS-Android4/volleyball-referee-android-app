package com.tonkar.volleyballreferee.ui.game.sanction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.tonkar.volleyballreferee.R;
import com.tonkar.volleyballreferee.engine.game.IGame;
import com.tonkar.volleyballreferee.engine.game.sanction.SanctionType;
import com.tonkar.volleyballreferee.engine.team.TeamType;
import com.tonkar.volleyballreferee.ui.team.PlayerToggleButton;
import com.tonkar.volleyballreferee.ui.util.UiUtils;

public class DelaySanctionSelectionFragment extends Fragment {

    private SanctionSelectionDialog mSanctionSelectionDialog;
    private IGame                   mGame;
    private TeamType                mTeamType;
    private PlayerToggleButton      mDelayWarningButton;
    private PlayerToggleButton      mDelayPenaltyButton;
    private SanctionType            mSelectedDelaySanction;

    void init(SanctionSelectionDialog sanctionSelectionDialog, IGame game, TeamType teamType) {
        mSanctionSelectionDialog = sanctionSelectionDialog;
        mGame = game;
        mTeamType = teamType;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delay_sanction_selection, container, false);

        if (mGame != null && mTeamType != null) {
            mDelayWarningButton = view.findViewById(R.id.delay_warning_button);
            mDelayPenaltyButton = view.findViewById(R.id.delay_penalty_button);

            mDelayWarningButton.setColor(getContext(), mGame.getTeamColor(mTeamType));
            mDelayPenaltyButton.setColor(getContext(), mGame.getTeamColor(mTeamType));

            mDelayWarningButton.setOnCheckedChangeListener((cButton, isChecked) -> {
                UiUtils.animate(getContext(), cButton);
                if (isChecked) {
                    mSelectedDelaySanction = SanctionType.DELAY_WARNING;
                    mDelayPenaltyButton.setChecked(false);
                    mSanctionSelectionDialog.computeOkAvailability(R.id.delay_sanction_tab);
                }
            });

            mDelayPenaltyButton.setOnCheckedChangeListener((cButton, isChecked) -> {
                UiUtils.animate(getContext(), cButton);
                if (isChecked) {
                    mSelectedDelaySanction = SanctionType.DELAY_PENALTY;
                    mDelayWarningButton.setChecked(false);
                    mSanctionSelectionDialog.computeOkAvailability(R.id.delay_sanction_tab);
                }
            });


            SanctionType possibleDelaySanction = mGame.getPossibleDelaySanction(mTeamType);

            ViewGroup delayWarningLayout = view.findViewById(R.id.delay_warning_layout);
            ViewGroup delayPenaltyLayout = view.findViewById(R.id.delay_penalty_layout);
            delayWarningLayout.setVisibility(SanctionType.DELAY_WARNING.equals(possibleDelaySanction) ? View.VISIBLE : View.GONE);
            delayPenaltyLayout.setVisibility(SanctionType.DELAY_PENALTY.equals(possibleDelaySanction) ? View.VISIBLE : View.GONE);
        }

        return view;
    }

    SanctionType getSelectedDelaySanction() {
        return mSelectedDelaySanction;
    }
}
