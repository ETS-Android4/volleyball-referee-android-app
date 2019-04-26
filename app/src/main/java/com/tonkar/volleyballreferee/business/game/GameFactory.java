package com.tonkar.volleyballreferee.business.game;

import android.util.Log;

import com.tonkar.volleyballreferee.interfaces.GameService;
import com.tonkar.volleyballreferee.interfaces.Tags;
import com.tonkar.volleyballreferee.interfaces.UsageType;
import com.tonkar.volleyballreferee.interfaces.data.StoredGameService;
import com.tonkar.volleyballreferee.business.rules.Rules;

public class GameFactory {

    public static IndoorGame createIndoorGame(final long gameDate, final long gameSchedule, final Rules rules) {
        Log.i(Tags.FACTORY, "Create indoor game rules");
        return new IndoorGame(gameDate, gameSchedule, rules);
    }

    public static BeachGame createBeachGame(final long gameDate, final long gameSchedule, final Rules rules) {
        Log.i(Tags.FACTORY, "Create beach game");
        return new BeachGame(gameDate, gameSchedule, rules);
    }

    public static IndoorGame createPointBasedGame(final long gameDate, final long gameSchedule, final Rules rules) {
        Log.i(Tags.FACTORY, "Create score-based game");
        IndoorGame game = createIndoorGame(gameDate, gameSchedule, rules);
        game.setUsage(UsageType.POINTS_SCOREBOARD);
        return game;
    }

    public static TimeBasedGame createTimeBasedGame(final long gameDate, final long gameSchedule) {
        Log.i(Tags.FACTORY, "Create time-based game");
        return new TimeBasedGame(gameDate, gameSchedule);
    }

    public static Indoor4x4Game createIndoor4x4Game(final long gameDate, final long gameSchedule, final Rules rules) {
        Log.i(Tags.FACTORY, "Create indoor 4x4 game");
        return new Indoor4x4Game(gameDate, gameSchedule, rules);
    }

    public static GameService createGame(StoredGameService storedGameService) {
        Log.i(Tags.FACTORY, "Create game from web");
        GameService gameService = null;

        switch (storedGameService.getKind()) {
            case INDOOR:
                IndoorGame indoorGame = createIndoorGame(storedGameService.getGameDate(), storedGameService.getGameSchedule(), storedGameService.getRules());
                indoorGame.setUsage(storedGameService.getUsage());
                indoorGame.setIndexed(storedGameService.isIndexed());
                indoorGame.setLeagueName(storedGameService.getLeagueName());
                indoorGame.setDivisionName(storedGameService.getDivisionName());
                indoorGame.restoreTeams(storedGameService);
                gameService = indoorGame;
                break;
            case BEACH:
                BeachGame beachGame = createBeachGame(storedGameService.getGameDate(), storedGameService.getGameSchedule(), storedGameService.getRules());
                beachGame.setIndexed(storedGameService.isIndexed());
                beachGame.setLeagueName(storedGameService.getLeagueName());
                beachGame.setDivisionName(storedGameService.getDivisionName());
                beachGame.restoreTeams(storedGameService);
                gameService = beachGame;
                break;
            case INDOOR_4X4:
                Indoor4x4Game indoor4x4Game = createIndoor4x4Game(storedGameService.getGameDate(), storedGameService.getGameSchedule(), storedGameService.getRules());
                indoor4x4Game.setUsage(storedGameService.getUsage());
                indoor4x4Game.setIndexed(storedGameService.isIndexed());
                indoor4x4Game.setLeagueName(storedGameService.getLeagueName());
                indoor4x4Game.setDivisionName(storedGameService.getDivisionName());
                indoor4x4Game.restoreTeams(storedGameService);
                gameService = indoor4x4Game;
                break;
        }

        return gameService;
    }
}
