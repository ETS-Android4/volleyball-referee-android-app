package com.tonkar.volleyballreferee.ui.game;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tonkar.volleyballreferee.R;
import com.tonkar.volleyballreferee.business.ServicesProvider;
import com.tonkar.volleyballreferee.interfaces.ActionOriginType;
import com.tonkar.volleyballreferee.interfaces.team.IndoorTeamService;
import com.tonkar.volleyballreferee.interfaces.team.PositionType;
import com.tonkar.volleyballreferee.interfaces.team.TeamListener;
import com.tonkar.volleyballreferee.interfaces.team.TeamType;

import androidx.fragment.app.Fragment;

public class SubstitutionsFragment extends Fragment implements TeamListener {

    private IndoorTeamService        mIndoorTeamService;
    private SubstitutionsListAdapter mLeftTeamSubstitutionsListAdapter;
    private SubstitutionsListAdapter mRightTeamSubstitutionsListAdapter;

    public SubstitutionsFragment() {}

    public static SubstitutionsFragment newInstance() {
        SubstitutionsFragment fragment = new SubstitutionsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("VBR-Substitutions", "Create substitutions fragment");
        View view = inflater.inflate(R.layout.fragment_substitutions, container, false);

        mIndoorTeamService = (IndoorTeamService) ServicesProvider.getInstance().getTeamService();
        mIndoorTeamService.addTeamListener(this);

        ListView leftTeamSubstitutionsList = view.findViewById(R.id.left_team_substitutions_list);
        mLeftTeamSubstitutionsListAdapter = new SubstitutionsListAdapter(getActivity(), inflater, mIndoorTeamService, mIndoorTeamService.getTeamOnLeftSide());
        leftTeamSubstitutionsList.setAdapter(mLeftTeamSubstitutionsListAdapter);

        ListView rightTeamSubstitutionsList = view.findViewById(R.id.right_team_substitutions_list);
        mRightTeamSubstitutionsListAdapter = new SubstitutionsListAdapter(getActivity(), inflater, mIndoorTeamService, mIndoorTeamService.getTeamOnRightSide());
        rightTeamSubstitutionsList.setAdapter(mRightTeamSubstitutionsListAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIndoorTeamService.removeTeamListener(this);
    }

    @Override
    public void onTeamsSwapped(TeamType leftTeamType, TeamType rightTeamType, ActionOriginType actionOriginType) {
        mLeftTeamSubstitutionsListAdapter.setTeamType(leftTeamType);
        mRightTeamSubstitutionsListAdapter.setTeamType(rightTeamType);
        mLeftTeamSubstitutionsListAdapter.notifyDataSetChanged();
        mRightTeamSubstitutionsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPlayerChanged(TeamType teamType, int number, PositionType positionType, ActionOriginType actionOriginType) {
        if (ActionOriginType.USER.equals(actionOriginType)) {
            if (teamType.equals(mLeftTeamSubstitutionsListAdapter.getTeamType())) {
                mLeftTeamSubstitutionsListAdapter.notifyDataSetChanged();
            } else {
                mRightTeamSubstitutionsListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onTeamRotated(TeamType teamType) {}

}
