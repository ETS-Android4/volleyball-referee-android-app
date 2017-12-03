package com.tonkar.volleyballreferee.business.game;

import com.tonkar.volleyballreferee.business.team.IndoorTeamComposition;
import com.tonkar.volleyballreferee.business.team.IndoorTeamDefinition;
import com.tonkar.volleyballreferee.business.team.TeamDefinition;
import com.tonkar.volleyballreferee.interfaces.IndoorTeamService;
import com.tonkar.volleyballreferee.interfaces.PositionType;
import com.tonkar.volleyballreferee.interfaces.Substitution;
import com.tonkar.volleyballreferee.rules.Rules;
import com.tonkar.volleyballreferee.interfaces.ActionOriginType;
import com.tonkar.volleyballreferee.interfaces.GameType;
import com.tonkar.volleyballreferee.interfaces.TeamType;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class IndoorGame extends Game implements IndoorTeamService {

    IndoorGame(final Rules rules) {
        super(GameType.INDOOR, rules);
    }

    @Override
    protected TeamDefinition createTeamDefinition(TeamType teamType) {
        return new IndoorTeamDefinition(teamType);
    }

    @Override
    protected Set createSet(Rules rules, boolean isTieBreakSet, TeamType servingTeamAtStart) {
        return new IndoorSet(rules, isTieBreakSet ? 15 : rules.getPointsPerSet(), servingTeamAtStart);
    }

    private IndoorTeamDefinition getIndoorTeamDefinition(TeamType teamType) {
        return (IndoorTeamDefinition) getTeamDefinition(teamType);
    }

    private IndoorTeamComposition getIndoorTeamComposition(TeamType teamType) {
        return (IndoorTeamComposition) currentSet().getTeamComposition(teamType);
    }

    @Override
    public void addPoint(final TeamType teamType) {
        super.addPoint(teamType);

        if (!currentSet().isSetCompleted()) {
            checkPosition1(teamType);

            final int leadingScore = currentSet().getPoints(currentSet().getLeadingTeam());

            // In indoor volley, the teams change sides after the 8th during the tie break
            if (isTieBreakSet() && leadingScore == 8 && currentSet().getPoints(TeamType.HOME) != currentSet().getPoints(TeamType.GUEST)) {
                swapTeams(ActionOriginType.APPLICATION);
            }

            // In indoor volley, there are two technical timeouts at 8 and 16 but not during tie break
            if (getRules().areTechnicalTimeoutsEnabled()
                    && !isTieBreakSet()
                    && currentSet().getLeadingTeam().equals(teamType)
                    && (leadingScore == 8 || leadingScore == 16)
                    && currentSet().getPoints(TeamType.HOME) != currentSet().getPoints(TeamType.GUEST)) {
                notifyTechnicalTimeoutReached();
            }
        }
    }

    @Override
    public void removeLastPoint() {
        super.removeLastPoint();

        checkPosition1(getServingTeam());

        final int leadingScore = currentSet().getPoints(currentSet().getLeadingTeam());

        // In indoor volley, the teams change sides after the 8th during the tie break
        if (isTieBreakSet() && leadingScore == 7) {
            swapTeams(ActionOriginType.APPLICATION);
        }
    }

    private void checkPosition1(final TeamType scoringTeam) {
        int number = getIndoorTeamComposition(scoringTeam).checkPosition1Offence();
        if (number > 0)  {
            substitutePlayer(scoringTeam, number, PositionType.POSITION_1, ActionOriginType.APPLICATION);
        }

        TeamType defendingTeam = scoringTeam.other();
        number = getIndoorTeamComposition(defendingTeam).checkPosition1Defence();
        if (number > 0)  {
            substitutePlayer(defendingTeam, number, PositionType.POSITION_1, ActionOriginType.APPLICATION);
        }
    }

    @Override
    public void substitutePlayer(TeamType teamType, int number, PositionType positionType, ActionOriginType actionOriginType) {
        if (getIndoorTeamComposition(teamType).substitutePlayer(number, positionType)) {
            notifyPlayerChanged(teamType, number, positionType, actionOriginType);
        }
    }

    @Override
    public java.util.Set<Integer> getPossibleSubstitutions(TeamType teamType, PositionType positionType) {
        return getIndoorTeamComposition(teamType).getPossibleSubstitutions(positionType);
    }

    @Override
    public void confirmStartingLineup() {
        getIndoorTeamComposition(TeamType.HOME).confirmStartingLineup();
        getIndoorTeamComposition(TeamType.GUEST).confirmStartingLineup();
    }

    @Override
    public boolean isStartingLineupConfirmed() {
        return getIndoorTeamComposition(TeamType.HOME).isStartingLineupConfirmed() && getIndoorTeamComposition(TeamType.GUEST).isStartingLineupConfirmed();
    }

    @Override
    public java.util.Set<Integer> getPlayersInStartingLineup(TeamType teamType, int setIndex) {
        java.util.Set<Integer> players = new TreeSet<>();

        Set set = getSet(setIndex);

        if (set != null) {
            IndoorTeamComposition indoorTeamComposition = (IndoorTeamComposition) set.getTeamComposition(teamType);
            players = indoorTeamComposition.getPlayersInStartingLineup();
        }

        return players;
    }

    @Override
    public PositionType getPlayerPositionInStartingLineup(TeamType teamType, int number, int setIndex) {
        PositionType positionType = null;

        Set set = getSet(setIndex);

        if (set != null) {
            IndoorTeamComposition indoorTeamComposition = (IndoorTeamComposition) set.getTeamComposition(teamType);
            positionType = indoorTeamComposition.getPlayerPositionInStartingLineup(number);
        }

        return positionType;
    }

    @Override
    public int getPlayerAtPositionInStartingLineup(TeamType teamType, PositionType positionType, int setIndex) {
        int number = -1;

        Set set = getSet(setIndex);

        if (set != null) {
            IndoorTeamComposition indoorTeamComposition = (IndoorTeamComposition) set.getTeamComposition(teamType);
            number = indoorTeamComposition.getPlayerAtPositionInStartingLineup(positionType);
        }

        return number;
    }

    @Override
    public int getLiberoColor(TeamType teamType) {
        return getIndoorTeamDefinition(teamType).getLiberoColor();
    }

    @Override
    public void setLiberoColor(TeamType teamType, int color) {
        getIndoorTeamDefinition(teamType).setLiberoColor(color);
    }

    @Override
    public void addLibero(TeamType teamType, int number) {
        getIndoorTeamDefinition(teamType).addLibero(number);
    }

    @Override
    public void removeLibero(TeamType teamType, int number) {
        getIndoorTeamDefinition(teamType).removeLibero(number);
    }

    @Override
    public boolean isLibero(TeamType teamType, int number) {
        return getIndoorTeamDefinition(teamType).isLibero(number);
    }

    @Override
    public boolean canAddLibero(TeamType teamType) {
        return getIndoorTeamDefinition(teamType).canAddLibero();
    }

    @Override
    public List<Substitution> getSubstitutions(TeamType teamType) {
        return getIndoorTeamComposition(teamType).getSubstitutions();
    }

    @Override
    public List<Substitution> getSubstitutions(TeamType teamType, int setIndex) {
        List<Substitution> substitutions = new ArrayList<>();

        Set set = getSet(setIndex);

        if (set != null) {
            IndoorTeamComposition indoorTeamComposition = (IndoorTeamComposition) set.getTeamComposition(teamType);
            substitutions = indoorTeamComposition.getSubstitutions();
        }

        return substitutions;
    }

}
