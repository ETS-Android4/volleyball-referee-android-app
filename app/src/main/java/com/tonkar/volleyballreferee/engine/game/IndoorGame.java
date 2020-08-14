package com.tonkar.volleyballreferee.engine.game;

import com.tonkar.volleyballreferee.engine.game.sanction.SanctionType;
import com.tonkar.volleyballreferee.engine.game.set.IndoorSet;
import com.tonkar.volleyballreferee.engine.rules.Rules;
import com.tonkar.volleyballreferee.engine.stored.IStoredGame;
import com.tonkar.volleyballreferee.engine.stored.api.ApiCourt;
import com.tonkar.volleyballreferee.engine.stored.api.ApiPlayer;
import com.tonkar.volleyballreferee.engine.stored.api.ApiSanction;
import com.tonkar.volleyballreferee.engine.stored.api.ApiSubstitution;
import com.tonkar.volleyballreferee.engine.stored.api.ApiTimeout;
import com.tonkar.volleyballreferee.engine.team.IClassicTeam;
import com.tonkar.volleyballreferee.engine.team.TeamType;
import com.tonkar.volleyballreferee.engine.team.composition.IndoorTeamComposition;
import com.tonkar.volleyballreferee.engine.team.definition.IndoorTeamDefinition;
import com.tonkar.volleyballreferee.engine.team.definition.TeamDefinition;
import com.tonkar.volleyballreferee.engine.team.player.PositionType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class IndoorGame extends Game implements IClassicTeam {

    public IndoorGame(String id, String createdBy, String refereeName, long createdAt, long scheduledAt, Rules rules) {
        super(GameType.INDOOR, id, createdBy, refereeName, createdAt, scheduledAt, rules);
    }

    // For GSON Deserialization
    public IndoorGame() {
        this("", "", "", 0L, 0L, new Rules());
    }

    @Override
    protected TeamDefinition createTeamDefinition(TeamType teamType) {
        return new IndoorTeamDefinition(GameType.INDOOR, UUID.randomUUID().toString(), getCreatedBy(), teamType);
    }

    @Override
    protected com.tonkar.volleyballreferee.engine.game.set.Set createSet(Rules rules, int pointsToWinSet, TeamType servingTeamAtStart) {
        return new IndoorSet(getRules(), pointsToWinSet, servingTeamAtStart, getTeamDefinition(TeamType.HOME), getTeamDefinition(TeamType.GUEST));
    }

    private IndoorTeamComposition getIndoorTeamComposition(TeamType teamType) {
        return (IndoorTeamComposition) currentSet().getTeamComposition(teamType);
    }

    private IndoorTeamComposition getIndoorTeamComposition(TeamType teamType, int setIndex) {
        return (IndoorTeamComposition) getSet(setIndex).getTeamComposition(teamType);
    }

    @Override
    public void addPoint(final TeamType teamType) {
        super.addPoint(teamType);

        if (!currentSet().isSetCompleted()) {
            checkPosition1(teamType);

            final int leadingScore = currentSet().getPoints(currentSet().getLeadingTeam());

            // In indoor volley, the teams change sides after the 8th during the tie break
            if (isTieBreakSet() && leadingScore == 8 && currentSet().getPoints(TeamType.HOME) != currentSet().getPoints(TeamType.GUEST) && teamType.equals(currentSet().getLeadingTeam())) {
                swapTeams(ActionOriginType.APPLICATION);
            }

            // In indoor volley, there are two technical timeouts at 8 and 16 but not during tie break
            if (getRules().isTechnicalTimeouts()
                    && !isTieBreakSet()
                    && currentSet().getLeadingTeam().equals(teamType)
                    && (leadingScore == 8 || leadingScore == 16)
                    && currentSet().getPoints(TeamType.HOME) != currentSet().getPoints(TeamType.GUEST)) {
                notifyTechnicalTimeoutReached();
            }

            // Specific custom rule
            if (samePlayerServedNConsecutiveTimes(teamType, getPoints(teamType), getPointsLadder())) {
                rotateToNextPositions(teamType);
            }
        }
    }

    @Override
    public void removeLastPoint() {
        final TeamType oldServingTeam = getServingTeam();
        final int oldLeadingScore = currentSet().getPoints(currentSet().getLeadingTeam());
        super.removeLastPoint();

        final TeamType newServingTeam = getServingTeam();
        checkPosition1(newServingTeam);
        final int leadingScore = currentSet().getPoints(currentSet().getLeadingTeam());

        // In indoor volley, the teams change sides after the 8th during the tie break
        if (isTieBreakSet() && leadingScore == 7 && oldLeadingScore == 8) {
            swapTeams(ActionOriginType.APPLICATION);
        }

        // Specific custom rule
        if (oldServingTeam.equals(newServingTeam) && samePlayerHadServedNConsecutiveTimes(oldServingTeam, getPoints(oldServingTeam), getPointsLadder())) {
            rotateToPreviousPositions(oldServingTeam);
        }
    }

    private void checkPosition1(final TeamType scoringTeam) {
        int number = getIndoorTeamComposition(scoringTeam).checkPosition1Offence();
        if (number > -1)  {
            substitutePlayer(scoringTeam, number, PositionType.POSITION_1, ActionOriginType.APPLICATION);
        }

        TeamType defendingTeam = scoringTeam.other();
        number = getIndoorTeamComposition(defendingTeam).checkPosition1Defence();
        if (number > -1)  {
            substitutePlayer(defendingTeam, number, PositionType.POSITION_1, ActionOriginType.APPLICATION);
        }
    }

    @Override
    public void substitutePlayer(TeamType teamType, int number, PositionType positionType, ActionOriginType actionOriginType) {
        if (getIndoorTeamComposition(teamType).substitutePlayer(number, positionType, getPoints(TeamType.HOME), getPoints(TeamType.GUEST))) {
            notifyPlayerChanged(teamType, number, positionType, actionOriginType);
        }
    }

    @Override
    protected void undoSubstitution(TeamType teamType, ApiSubstitution substitution) {
        Set<Integer> evictedPlayers = getEvictedPlayersForCurrentSet(teamType, true, true);
        if (!evictedPlayers.contains(substitution.getPlayerIn())
                && !evictedPlayers.contains(substitution.getPlayerOut())
                && getIndoorTeamComposition(teamType).undoSubstitution(substitution)) {
            notifyPlayerChanged(teamType, substitution.getPlayerOut(), getIndoorTeamComposition(teamType).getPlayerPosition(substitution.getPlayerOut()), ActionOriginType.APPLICATION);
        }
    }

    @Override
    public Set<Integer> getPossibleSubstitutions(TeamType teamType, PositionType positionType) {
        return getIndoorTeamComposition(teamType).getPossibleSubstitutions(positionType);
    }

    @Override
    public void confirmStartingLineup(TeamType teamType) {
        getIndoorTeamComposition(teamType).confirmStartingLineup();
        notifyStartingLineupSubmitted(teamType);
    }

    @Override
    public boolean hasActingCaptainOnCourt(TeamType teamType) {
        return getIndoorTeamComposition(teamType).hasActingCaptainOnCourt();
    }

    @Override
    public int getActingCaptain(TeamType teamType, int setIndex) {
        int number = -1;

        com.tonkar.volleyballreferee.engine.game.set.Set set = getSet(setIndex);

        if (set != null) {
            IndoorTeamComposition indoorTeamComposition = (IndoorTeamComposition) set.getTeamComposition(teamType);
            number = indoorTeamComposition.getActingCaptain();
        }

        return number;
    }

    @Override
    public void setActingCaptain(TeamType teamType, int number) {
        getIndoorTeamComposition(teamType).setActingCaptain(number);
    }

    @Override
    public boolean isActingCaptain(TeamType teamType, int number) {
        return getIndoorTeamComposition(teamType).isActingCaptain(number);
    }

    @Override
    public Set<Integer> getPossibleActingCaptains(TeamType teamType) {
        return getIndoorTeamComposition(teamType).getPossibleActingCaptains();
    }

    @Override
    public boolean hasRemainingSubstitutions(TeamType teamType) {
        return getIndoorTeamComposition(teamType).canSubstitute();
    }

    @Override
    public int countRemainingSubstitutions(TeamType teamType) {
        return getIndoorTeamComposition(teamType).countRemainingSubstitutions();
    }

    @Override
    public Set<Integer> filterSubstitutionsWithEvictedPlayersForCurrentSet(TeamType teamType, int evictedNumber, Set<Integer> possibleSubstitutions) {
        final Set<Integer> filteredSubstitutions = new HashSet<>(possibleSubstitutions);
        final Set<Integer> evictedPlayers = getEvictedPlayersForCurrentSet(teamType, true, true);

        for (Iterator<Integer> iterator = filteredSubstitutions.iterator(); iterator.hasNext();) {
            int possibleReplacement = iterator.next();
            if (evictedPlayers.contains(possibleReplacement)) {
                iterator.remove();
            } else if (!isLibero(teamType, evictedNumber) && isLibero(teamType, possibleReplacement)) {
                iterator.remove();
            }
        }

        return filteredSubstitutions;
    }

    @Override
    public int getWaitingMiddleBlocker(TeamType teamType) {
        return getIndoorTeamComposition(teamType).getWaitingMiddleBlocker();
    }

    @Override
    public boolean isStartingLineupConfirmed(TeamType teamType) {
        return getIndoorTeamComposition(teamType).isStartingLineupConfirmed();
    }

    @Override
    public boolean isStartingLineupConfirmed(TeamType teamType, int setIndex) {
        return getIndoorTeamComposition(teamType, setIndex).isStartingLineupConfirmed();
    }

    @Override
    public ApiCourt getStartingLineup(TeamType teamType, int setIndex) {
        ApiCourt startingLineup = new ApiCourt();

        com.tonkar.volleyballreferee.engine.game.set.Set set = getSet(setIndex);

        if (set != null) {
            IndoorTeamComposition indoorTeamComposition = (IndoorTeamComposition) set.getTeamComposition(teamType);
            startingLineup = indoorTeamComposition.getStartingLineup();
        }

        return startingLineup;
    }

    @Override
    public PositionType getPlayerPositionInStartingLineup(TeamType teamType, int number, int setIndex) {
        PositionType positionType = null;

        com.tonkar.volleyballreferee.engine.game.set.Set set = getSet(setIndex);

        if (set != null) {
            IndoorTeamComposition indoorTeamComposition = (IndoorTeamComposition) set.getTeamComposition(teamType);
            positionType = indoorTeamComposition.getPlayerPositionInStartingLineup(number);
        }

        return positionType;
    }

    @Override
    public int getPlayerAtPositionInStartingLineup(TeamType teamType, PositionType positionType, int setIndex) {
        int number = -1;

        com.tonkar.volleyballreferee.engine.game.set.Set set = getSet(setIndex);

        if (set != null) {
            IndoorTeamComposition indoorTeamComposition = (IndoorTeamComposition) set.getTeamComposition(teamType);
            number = indoorTeamComposition.getPlayerAtPositionInStartingLineup(positionType);
        }

        return number;
    }

    @Override
    public int getLiberoColor(TeamType teamType) {
        return getTeamDefinition(teamType).getLiberoColorInt();
    }

    @Override
    public void setLiberoColor(TeamType teamType, int color) {
        getTeamDefinition(teamType).setLiberoColorInt(color);
    }

    @Override
    public void addLibero(TeamType teamType, int number) {
        getTeamDefinition(teamType).addLibero(number);
    }

    @Override
    public void removeLibero(TeamType teamType, int number) {
        getTeamDefinition(teamType).removeLibero(number);
    }

    @Override
    public boolean isLibero(TeamType teamType, int number) {
        return getTeamDefinition(teamType).isLibero(number);
    }

    @Override
    public boolean canAddLibero(TeamType teamType) {
        return getTeamDefinition(teamType).canAddLibero();
    }

    @Override
    public Set<ApiPlayer> getLiberos(TeamType teamType) {
        return new TreeSet<>(getTeamDefinition(teamType).getLiberos());
    }

    @Override
    public List<ApiSubstitution> getSubstitutions(TeamType teamType) {
        return getIndoorTeamComposition(teamType).getSubstitutions();
    }

    @Override
    public List<ApiSubstitution> getSubstitutions(TeamType teamType, int setIndex) {
        List<ApiSubstitution> substitutions = new ArrayList<>();

        com.tonkar.volleyballreferee.engine.game.set.Set set = getSet(setIndex);

        if (set != null) {
            IndoorTeamComposition indoorTeamComposition = (IndoorTeamComposition) set.getTeamComposition(teamType);
            substitutions = indoorTeamComposition.getSubstitutions();
        }

        return substitutions;
    }

    @Override
    public void giveSanction(TeamType teamType, SanctionType sanctionType, int number) {
        super.giveSanction(teamType, sanctionType, number);

        if (ApiSanction.isPlayer(number) && (sanctionType.isMisconductExpulsionCard() || sanctionType.isMisconductDisqualificationCard())) {
            // The player excluded for the set/match has to be legally replaced
            PositionType positionType = getPlayerPosition(teamType, number);

            if (!PositionType.BENCH.equals(positionType)) {
                final Set<Integer> possibleSubstitutions = getPossibleSubstitutions(teamType, positionType);
                final Set<Integer> filteredSubstitutions = filterSubstitutionsWithEvictedPlayersForCurrentSet(teamType, number, possibleSubstitutions);

                // If there is no possible legal substitution, the set is lost
                if (filteredSubstitutions.size() == 0) {
                    forceFinishSet(teamType.other());
                }
            }
        }

        if (ApiSanction.isPlayer(number) && sanctionType.isMisconductDisqualificationCard() && !isMatchCompleted()) {
            // check that the team has enough players to continue the match
            // copy the list of players
            List<ApiPlayer> players = new ArrayList<>(getTeamDefinition(teamType).getPlayers());

            // first remove the liberos

            for (ApiPlayer libero : getTeamDefinition(teamType).getLiberos()) {
                players.remove(libero);
            }

            // then remove the disqualified players

            for (ApiSanction sanction : getAllSanctions(teamType)) {
                if (sanction.getCard().isMisconductDisqualificationCard()) {
                    players.remove(new ApiPlayer(sanction.getNum()));
                }
            }

            if (players.size() < getExpectedNumberOfPlayersOnCourt()) {
                // not enough players: finish the match
                forceFinishMatch(teamType.other());
            }
        }
    }

    @Override
    public int getExpectedNumberOfPlayersOnCourt() {
        return getTeamDefinition(TeamType.HOME).getExpectedNumberOfPlayersOnCourt();
    }

    @Override
    public void restoreTeams(IStoredGame storedGame) {
        super.restoreTeams(storedGame);
    }

    @Override
    void restoreTeam(IStoredGame storedGame, TeamType teamType) {
        super.restoreTeam(storedGame, teamType);
        setLiberoColor(teamType, storedGame.getLiberoColor(teamType));

        for (ApiPlayer player : storedGame.getLiberos(teamType))  {
            addPlayer(teamType, player.getNum());
            addLibero(teamType, player.getNum());
            if (!player.getName().trim().isEmpty()) {
                setPlayerName(teamType, player.getNum(), player.getName());
            }
        }

        setCaptain(teamType, storedGame.getCaptain(teamType));
    }

    @Override
    public void restoreGame(IStoredGame storedGame) {
        if (GameStatus.LIVE.equals(storedGame.getMatchStatus())) {
            startMatch();

            for (int setIndex = 0; setIndex < storedGame.getNumberOfSets(); setIndex++) {
                List<TeamType> pointsLadder = storedGame.getPointsLadder(setIndex);

                if (storedGame.isStartingLineupConfirmed(TeamType.HOME)) {
                    ApiCourt homeStartingLineup = storedGame.getStartingLineup(TeamType.HOME, setIndex);

                    for (PositionType position : PositionType.listPositions(getKind())) {
                        substitutePlayer(TeamType.HOME, homeStartingLineup.getPlayerAt(position), position, ActionOriginType.USER);
                    }

                    confirmStartingLineup(TeamType.HOME);
                }

                if (storedGame.isStartingLineupConfirmed(TeamType.GUEST)) {
                    ApiCourt guestStartingLineup = storedGame.getStartingLineup(TeamType.GUEST, setIndex);

                    for (PositionType position : PositionType.listPositions(getKind())) {
                        substitutePlayer(TeamType.GUEST, guestStartingLineup.getPlayerAt(position), position, ActionOriginType.USER);
                    }

                    confirmStartingLineup(TeamType.GUEST);
                }

                getSet(setIndex).setServingTeamAtStart(storedGame.getFirstServingTeam(setIndex));

                for (int pointsIndex = 0; pointsIndex < pointsLadder.size(); pointsIndex++) {
                    int homePoints = getPoints(TeamType.HOME, setIndex);
                    int guestPoints = getPoints(TeamType.GUEST, setIndex);

                    List<ApiTimeout> homeTimeouts = storedGame.getTimeoutsIfExist(TeamType.HOME, setIndex, homePoints, guestPoints);
                    for (ApiTimeout timeout : homeTimeouts) {
                        callTimeout(TeamType.HOME);
                    }

                    List<ApiTimeout> guestTimeouts = storedGame.getTimeoutsIfExist(TeamType.GUEST, setIndex, homePoints, guestPoints);
                    for (ApiTimeout timeout : guestTimeouts) {
                        callTimeout(TeamType.GUEST);
                    }

                    List<ApiSubstitution> homeSubstitutions = storedGame.getSubstitutionsIfExist(TeamType.HOME, setIndex, homePoints, guestPoints);
                    for (ApiSubstitution substitution : homeSubstitutions) {
                        PositionType positionType = getPlayerPosition(TeamType.HOME, substitution.getPlayerOut(), setIndex);
                        substitutePlayer(TeamType.HOME, substitution.getPlayerIn(), positionType, ActionOriginType.USER);
                    }

                    List<ApiSubstitution> guestSubstitutions = storedGame.getSubstitutionsIfExist(TeamType.GUEST, setIndex, homePoints, guestPoints);
                    for (ApiSubstitution substitution : guestSubstitutions) {
                        PositionType positionType = getPlayerPosition(TeamType.GUEST, substitution.getPlayerOut(), setIndex);
                        substitutePlayer(TeamType.GUEST, substitution.getPlayerIn(), positionType, ActionOriginType.USER);
                    }

                    List<ApiSanction> homeSanctions = storedGame.getSanctionsIfExist(TeamType.HOME, setIndex, homePoints, guestPoints);
                    for (ApiSanction sanction : homeSanctions) {
                        giveSanction(TeamType.HOME, sanction.getCard(), sanction.getNum());
                    }

                    List<ApiSanction> guestSanctions = storedGame.getSanctionsIfExist(TeamType.GUEST, setIndex, homePoints, guestPoints);
                    for (ApiSanction sanction : guestSanctions) {
                        giveSanction(TeamType.GUEST, sanction.getCard(), sanction.getNum());
                    }

                    if (pointsIndex == pointsLadder.size() - 1) {
                        setActingCaptain(TeamType.HOME, storedGame.getActingCaptain(TeamType.HOME, setIndex));
                        setActingCaptain(TeamType.GUEST, storedGame.getActingCaptain(TeamType.GUEST, setIndex));
                    }

                    addPoint(pointsLadder.get(pointsIndex));
                }
            }
        }
    }
}
