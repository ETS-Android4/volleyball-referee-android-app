package com.tonkar.volleyballreferee;

import androidx.test.runner.AndroidJUnit4;

import com.tonkar.volleyballreferee.business.team.IndoorTeamComposition;
import com.tonkar.volleyballreferee.business.team.IndoorTeamDefinition;
import com.tonkar.volleyballreferee.interfaces.GameType;
import com.tonkar.volleyballreferee.interfaces.team.PositionType;
import com.tonkar.volleyballreferee.interfaces.team.TeamType;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class IndoorTeamCompositionTest {

    @Test
    public void defaultTeam() {
        IndoorTeamComposition team = new IndoorTeamComposition(new IndoorTeamDefinition(GameType.INDOOR, TeamType.HOME), 6);

        assertEquals(0, team.getTeamDefinition().getNumberOfPlayers());
        assertEquals(0, team.getPlayersOnCourt().size());
        assertFalse(team.getTeamDefinition().hasPlayer(5));
        assertFalse(team.getTeamDefinition().hasPlayer(-1));
        assertNull(team.getPlayerPosition(5));
    }

    @Test
    public void createPlayers() {
        IndoorTeamDefinition teamDefinition = new IndoorTeamDefinition(GameType.INDOOR, TeamType.HOME);
        int playerCount = 7;

        for (int index = 1; index <= playerCount; index++) {
            teamDefinition.addPlayer(index);
            assertEquals(index, teamDefinition.getNumberOfPlayers());
            assertTrue(teamDefinition.hasPlayer(index));
        }

        IndoorTeamComposition team = new IndoorTeamComposition(teamDefinition, 6);
        assertEquals(0, team.getPlayersOnCourt().size());

        for (int index = 1; index <= playerCount; index++) {
            assertEquals(PositionType.BENCH, team.getPlayerPosition(index));
        }
    }

    @Test
    public void createPlayers_liberoSelection() {
        IndoorTeamDefinition teamDefinition = createTeamWithNPlayers(6);
        assertFalse(teamDefinition.canAddLibero());

        teamDefinition = createTeamWithNPlayers(7);
        assertTrue(teamDefinition.canAddLibero());
        teamDefinition.addLibero(7);
        assertFalse(teamDefinition.canAddLibero());

        teamDefinition = createTeamWithNPlayers(8);
        assertTrue(teamDefinition.canAddLibero());
        teamDefinition.addLibero(5);
        assertTrue(teamDefinition.canAddLibero());
        teamDefinition.addLibero(2);
        assertFalse(teamDefinition.canAddLibero());

        teamDefinition = createTeamWithNPlayers(13);
        assertTrue(teamDefinition.canAddLibero());
        teamDefinition.addLibero(9);
        assertTrue(teamDefinition.canAddLibero());
        teamDefinition.addLibero(1);
        assertFalse(teamDefinition.canAddLibero());
    }

    private IndoorTeamDefinition createTeamWithNPlayers(int playerCount) {
        IndoorTeamDefinition teamDefinition = new IndoorTeamDefinition(GameType.INDOOR, TeamType.GUEST);

        for (int index = 1; index <= playerCount; index++) {
            teamDefinition.addPlayer(index);
        }

        return teamDefinition;
    }

    private IndoorTeamComposition createTeamWithNPlayersAndFillCourt(int playerCount) {
        IndoorTeamDefinition teamDefinition = createTeamWithNPlayers(playerCount);
        teamDefinition.setCaptain(3);
        IndoorTeamComposition team = new IndoorTeamComposition(teamDefinition, 6);
        int playersOnCourt = 6;

        for (int index = 1; index <= playersOnCourt; index++) {
            team.substitutePlayer(index, PositionType.fromInt(index));
        }

        return team;
    }

    @Test
    public void substitution_fillCourt() {
        IndoorTeamDefinition teamDefinition = createTeamWithNPlayers(10);
        IndoorTeamComposition team = new IndoorTeamComposition(teamDefinition, 6);
        int playerCount = 6;

        assertEquals(0, team.getPlayersOnCourt().size());

        for (int index = 1; index <= playerCount; index++) {
            assertTrue(team.substitutePlayer(index, PositionType.fromInt(index)));
            assertEquals(PositionType.fromInt(index), team.getPlayerPosition(index));
            assertEquals(index, team.getPlayersOnCourt().size());
        }

    }

    @Test
    public void substitution_changePlayer_free() {
        IndoorTeamComposition team = createTeamWithNPlayersAndFillCourt(10);
        assertTrue(team.substitutePlayer(7, PositionType.POSITION_4));
        assertEquals(PositionType.BENCH, team.getPlayerPosition(4));
        assertTrue(team.substitutePlayer(4, PositionType.POSITION_6));
        assertEquals(PositionType.BENCH, team.getPlayerPosition(6));
        Set<Integer> availablePlayers = team.getPossibleSubstitutions(PositionType.POSITION_1);
        assertEquals(4, availablePlayers.size());
        assertTrue(availablePlayers.contains(6));
        assertTrue(availablePlayers.contains(8));
        assertTrue(availablePlayers.contains(9));
        assertTrue(availablePlayers.contains(10));
    }

    @Test
    public void substitution_changePlayer_confirmed_front() {
        IndoorTeamComposition team = createTeamWithNPlayersAndFillCourt(10);
        team.confirmStartingLineup();
        assertTrue(team.substitutePlayer(7, PositionType.POSITION_4));
        assertEquals(PositionType.BENCH, team.getPlayerPosition(4));

        Set<Integer> availablePlayers = team.getPossibleSubstitutions(PositionType.POSITION_4);
        assertEquals(1, availablePlayers.size());
        assertTrue(availablePlayers.contains(4));

        assertFalse(team.substitutePlayer(4, PositionType.POSITION_6));
        assertEquals(PositionType.BENCH, team.getPlayerPosition(4));

        assertTrue(team.substitutePlayer(4, PositionType.POSITION_4));
        assertEquals(PositionType.BENCH, team.getPlayerPosition(7));

        availablePlayers = team.getPossibleSubstitutions(PositionType.POSITION_4);
        assertEquals(0, availablePlayers.size());

        assertFalse(team.substitutePlayer(7, PositionType.POSITION_4));
        assertEquals(PositionType.BENCH, team.getPlayerPosition(7));

        IndoorTeamDefinition teamDefinition = (IndoorTeamDefinition) team.getTeamDefinition();
        teamDefinition.addLibero(10);
        availablePlayers = team.getPossibleSubstitutions(PositionType.POSITION_4);
        assertEquals(0, availablePlayers.size());
    }

    @Test
    public void substitution_changePlayer_confirmed_back() {
        IndoorTeamComposition team = createTeamWithNPlayersAndFillCourt(10);
        team.confirmStartingLineup();
        assertTrue(team.substitutePlayer(7, PositionType.POSITION_6));
        assertEquals(PositionType.BENCH, team.getPlayerPosition(6));

        Set<Integer> availablePlayers = team.getPossibleSubstitutions(PositionType.POSITION_6);
        assertEquals(1, availablePlayers.size());
        assertTrue(availablePlayers.contains(6));

        assertFalse(team.substitutePlayer(6, PositionType.POSITION_3));
        assertEquals(PositionType.BENCH, team.getPlayerPosition(6));

        assertTrue(team.substitutePlayer(6, PositionType.POSITION_6));
        assertEquals(PositionType.BENCH, team.getPlayerPosition(7));

        availablePlayers = team.getPossibleSubstitutions(PositionType.POSITION_6);
        assertEquals(0, availablePlayers.size());

        assertFalse(team.substitutePlayer(7, PositionType.POSITION_6));
        assertEquals(PositionType.BENCH, team.getPlayerPosition(7));

        IndoorTeamDefinition teamDefinition = (IndoorTeamDefinition) team.getTeamDefinition();
        teamDefinition.addLibero(10);
        availablePlayers = team.getPossibleSubstitutions(PositionType.POSITION_6);
        assertEquals(1, availablePlayers.size());
        assertTrue(availablePlayers.contains(10));
    }

    @Test
    public void substitution_changePlayer_libero() {
        IndoorTeamComposition team = createTeamWithNPlayersAndFillCourt(10);
        team.confirmStartingLineup();

        IndoorTeamDefinition teamDefinition = (IndoorTeamDefinition) team.getTeamDefinition();
        teamDefinition.addLibero(10);
        assertTrue(team.getPossibleSubstitutions(PositionType.POSITION_6).contains(10));
        assertFalse(team.getPossibleSubstitutions(PositionType.POSITION_4).contains(10));
        assertTrue(team.getPossibleSubstitutions(PositionType.POSITION_6).size() > 1);

        assertTrue(team.substitutePlayer(10, PositionType.POSITION_5));

        assertEquals(1, team.getPossibleSubstitutions(PositionType.POSITION_5).size());
        assertTrue(team.getPossibleSubstitutions(PositionType.POSITION_5).contains(5));
        assertFalse(team.getPossibleSubstitutions(PositionType.POSITION_6).contains(10));

        assertTrue(team.substitutePlayer(5, PositionType.POSITION_5));
        assertTrue(team.getPossibleSubstitutions(PositionType.POSITION_6).contains(10));
        assertTrue(team.getPossibleSubstitutions(PositionType.POSITION_6).size() > 1);

        teamDefinition.addLibero(11);
        assertFalse(team.getPossibleSubstitutions(PositionType.POSITION_6).contains(11));
        assertFalse(team.getPossibleSubstitutions(PositionType.POSITION_4).contains(11));

        teamDefinition.addLibero(9);
        assertTrue(team.getPossibleSubstitutions(PositionType.POSITION_6).contains(9));
        assertTrue(team.getPossibleSubstitutions(PositionType.POSITION_6).contains(10));
        assertFalse(team.getPossibleSubstitutions(PositionType.POSITION_4).contains(9));

        team.substitutePlayer(10, PositionType.POSITION_5);
        assertEquals(2, team.getPossibleSubstitutions(PositionType.POSITION_5).size());
        assertTrue(team.getPossibleSubstitutions(PositionType.POSITION_5).contains(5));
        assertFalse(team.getPossibleSubstitutions(PositionType.POSITION_5).contains(11));
    }

    @Test
    public void substitution_changePlayer_max() {
        IndoorTeamComposition team = createTeamWithNPlayersAndFillCourt(14);
        team.confirmStartingLineup();

        assertTrue(team.substitutePlayer(7, PositionType.POSITION_1));
        assertTrue(team.substitutePlayer(1, PositionType.POSITION_1));
        assertTrue(team.substitutePlayer(8, PositionType.POSITION_2));
        assertTrue(team.substitutePlayer(2, PositionType.POSITION_2));
        assertTrue(team.substitutePlayer(9, PositionType.POSITION_3));
        assertTrue(team.substitutePlayer(3, PositionType.POSITION_3));
        assertFalse(team.substitutePlayer(10, PositionType.POSITION_4));
    }

    @Test
    public void substitution_abnormal() {
        IndoorTeamComposition team = createTeamWithNPlayersAndFillCourt(10);
        assertFalse(team.substitutePlayer(18, PositionType.POSITION_1));

        IndoorTeamDefinition teamDefinition = (IndoorTeamDefinition) team.getTeamDefinition();
        teamDefinition.addLibero(10);
        assertFalse(team.substitutePlayer(10, PositionType.POSITION_4));
    }

    @Test
    public void rotation_next() {
        IndoorTeamComposition team = createTeamWithNPlayersAndFillCourt(10);
        team.confirmStartingLineup();

        team.rotateToNextPositions();
        assertEquals(PositionType.POSITION_6, team.getPlayerPosition(1));
        assertEquals(PositionType.POSITION_1, team.getPlayerPosition(2));
        assertEquals(PositionType.POSITION_2, team.getPlayerPosition(3));
        assertEquals(PositionType.POSITION_3, team.getPlayerPosition(4));
        assertEquals(PositionType.POSITION_4, team.getPlayerPosition(5));
        assertEquals(PositionType.POSITION_5, team.getPlayerPosition(6));
        assertEquals(PositionType.BENCH, team.getPlayerPosition(7));
    }

    @Test
    public void rotation_next_libero() {
        IndoorTeamComposition team = createTeamWithNPlayersAndFillCourt(10);
        IndoorTeamDefinition teamDefinition = (IndoorTeamDefinition) team.getTeamDefinition();
        teamDefinition.addLibero(10);
        team.confirmStartingLineup();
        assertTrue(team.substitutePlayer(10, PositionType.POSITION_6));

        team.rotateToNextPositions();
        assertEquals(PositionType.POSITION_6, team.getPlayerPosition(1));
        assertEquals(PositionType.POSITION_1, team.getPlayerPosition(2));
        assertEquals(PositionType.POSITION_2, team.getPlayerPosition(3));
        assertEquals(PositionType.POSITION_3, team.getPlayerPosition(4));
        assertEquals(PositionType.POSITION_4, team.getPlayerPosition(5));
        assertEquals(PositionType.POSITION_5, team.getPlayerPosition(10));
        assertEquals(PositionType.BENCH, team.getPlayerPosition(7));

        team.rotateToNextPositions();
        assertEquals(PositionType.POSITION_6, team.getPlayerPosition(2));
        assertEquals(PositionType.POSITION_1, team.getPlayerPosition(3));
        assertEquals(PositionType.POSITION_2, team.getPlayerPosition(4));
        assertEquals(PositionType.POSITION_3, team.getPlayerPosition(5));
        assertEquals(PositionType.POSITION_4, team.getPlayerPosition(6));
        assertEquals(PositionType.POSITION_5, team.getPlayerPosition(1));
        assertEquals(PositionType.BENCH, team.getPlayerPosition(7));

        assertEquals(10, team.checkPosition1Defence());
        assertEquals(-1, team.checkPosition1Offence());

        assertTrue(team.substitutePlayer(10, PositionType.POSITION_1));

        assertEquals(-1, team.checkPosition1Defence());
        assertEquals(3, team.checkPosition1Offence());
    }

    @Test
    public void rotation_previous() {
        IndoorTeamComposition team = createTeamWithNPlayersAndFillCourt(13);
        team.confirmStartingLineup();

        team.rotateToPreviousPositions();
        assertEquals(PositionType.POSITION_2, team.getPlayerPosition(1));
        assertEquals(PositionType.POSITION_3, team.getPlayerPosition(2));
        assertEquals(PositionType.POSITION_4, team.getPlayerPosition(3));
        assertEquals(PositionType.POSITION_5, team.getPlayerPosition(4));
        assertEquals(PositionType.POSITION_6, team.getPlayerPosition(5));
        assertEquals(PositionType.POSITION_1, team.getPlayerPosition(6));
        assertEquals(PositionType.BENCH, team.getPlayerPosition(7));
    }

    @Test
    public void rotation_previous_libero() {
        IndoorTeamComposition team = createTeamWithNPlayersAndFillCourt(12);
        IndoorTeamDefinition teamDefinition = (IndoorTeamDefinition) team.getTeamDefinition();
        teamDefinition.addLibero(10);
        team.confirmStartingLineup();

        assertTrue(team.substitutePlayer(10, PositionType.POSITION_1));

        team.rotateToPreviousPositions();
        assertEquals(PositionType.POSITION_2, team.getPlayerPosition(1));
        assertEquals(PositionType.POSITION_3, team.getPlayerPosition(2));
        assertEquals(PositionType.POSITION_4, team.getPlayerPosition(3));
        assertEquals(PositionType.POSITION_5, team.getPlayerPosition(10));
        assertEquals(PositionType.POSITION_6, team.getPlayerPosition(5));
        assertEquals(PositionType.POSITION_1, team.getPlayerPosition(6));
        assertEquals(PositionType.BENCH, team.getPlayerPosition(7));
    }

    @Test
    public void captain_definition() {
        IndoorTeamDefinition teamDefinition = createTeamWithNPlayers(13);

        teamDefinition.setCaptain(11);
        assertEquals(11, teamDefinition.getCaptain());
    }

    @Test
    public void captain_possible() {
        IndoorTeamDefinition teamDefinition = createTeamWithNPlayers(13);

        teamDefinition.addLibero(4);
        assertFalse(teamDefinition.getPossibleCaptains().contains(4));
    }

    @Test
    public void captain_composition() {
        IndoorTeamComposition teamComposition = createTeamWithNPlayersAndFillCourt(12);

        assertFalse(teamComposition.hasActingCaptainOnCourt());
        teamComposition.confirmStartingLineup();
        assertTrue(teamComposition.hasActingCaptainOnCourt());

        int captain = 3;
        assertEquals(captain, teamComposition.getActingCaptain());

        teamComposition.substitutePlayer(10, PositionType.POSITION_3);
        assertFalse(teamComposition.hasActingCaptainOnCourt());

        teamComposition.setActingCaptain(5);
        assertEquals(5, teamComposition.getActingCaptain());
        assertTrue(teamComposition.hasActingCaptainOnCourt());

        teamComposition.substitutePlayer(captain, PositionType.POSITION_3);
        assertEquals(captain, teamComposition.getActingCaptain());
        assertTrue(teamComposition.hasActingCaptainOnCourt());
    }

    private IndoorTeamComposition createTeamWith16PlayersAnd2LiberosAndFillCourt() {
        IndoorTeamDefinition teamDefinition = createTeamWithNPlayers(16);
        teamDefinition.addLibero(7);
        teamDefinition.addLibero(8);
        teamDefinition.setCaptain(3);
        IndoorTeamComposition team = new IndoorTeamComposition(teamDefinition, 6);
        int playersOnCourt = 6;

        for (int index = 1; index <= playersOnCourt; index++) {
            team.substitutePlayer(index, PositionType.fromInt(index));
        }

        return team;
    }

    @Test
    public void substitution_changePlayer_max_putLibero() {
        IndoorTeamComposition team = createTeamWith16PlayersAnd2LiberosAndFillCourt();
        team.confirmStartingLineup();

        assertTrue(team.substitutePlayer(11, PositionType.POSITION_1));
        assertTrue(team.substitutePlayer(1, PositionType.POSITION_1));
        assertTrue(team.substitutePlayer(12, PositionType.POSITION_2));
        assertTrue(team.substitutePlayer(2, PositionType.POSITION_2));
        assertTrue(team.substitutePlayer(13, PositionType.POSITION_3));
        assertTrue(team.substitutePlayer(3, PositionType.POSITION_3));

        assertEquals(0, team.getPossibleSubstitutions(PositionType.POSITION_4).size());
        // The 2 liberos
        assertEquals(2, team.getPossibleSubstitutions(PositionType.POSITION_6).size());

        assertTrue(team.substitutePlayer(7, PositionType.POSITION_5));
        assertTrue(team.substitutePlayer(8, PositionType.POSITION_5));
        assertTrue(team.substitutePlayer(7, PositionType.POSITION_5));
    }

    @Test
    public void substitution_changePlayer_max_changeLibero() {
        IndoorTeamComposition team = createTeamWith16PlayersAnd2LiberosAndFillCourt();
        team.confirmStartingLineup();

        assertTrue(team.substitutePlayer(11, PositionType.POSITION_1));
        assertTrue(team.substitutePlayer(1, PositionType.POSITION_1));
        assertTrue(team.substitutePlayer(12, PositionType.POSITION_2));
        assertTrue(team.substitutePlayer(2, PositionType.POSITION_2));

        assertTrue(team.substitutePlayer(15, PositionType.POSITION_5));
        assertTrue(team.substitutePlayer(7, PositionType.POSITION_6));
        // The other libero and #6
        assertEquals(2, team.getPossibleSubstitutions(PositionType.POSITION_6).size());
        assertTrue(team.substitutePlayer(5, PositionType.POSITION_5));
        assertEquals(0, team.getPossibleSubstitutions(PositionType.POSITION_3).size());

        // The other libero and #6
        assertEquals(2, team.getPossibleSubstitutions(PositionType.POSITION_6).size());
        assertTrue(team.substitutePlayer(8, PositionType.POSITION_6));
        assertEquals(2, team.getPossibleSubstitutions(PositionType.POSITION_6).size());
        assertTrue(team.substitutePlayer(7, PositionType.POSITION_6));
        assertEquals(2, team.getPossibleSubstitutions(PositionType.POSITION_6).size());
        assertTrue(team.substitutePlayer(6, PositionType.POSITION_6));
        // The 2 liberos
        assertEquals(2, team.getPossibleSubstitutions(PositionType.POSITION_6).size());

        assertTrue(team.substitutePlayer(8, PositionType.POSITION_1));
    }
}
