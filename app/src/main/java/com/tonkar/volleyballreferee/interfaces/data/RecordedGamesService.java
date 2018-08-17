package com.tonkar.volleyballreferee.interfaces.data;

import com.tonkar.volleyballreferee.business.data.GameDescription;
import com.tonkar.volleyballreferee.interfaces.GameService;

import java.util.List;
import java.util.Set;

public interface RecordedGamesService {

    String RECORDED_GAMES_FILE = "device_games_history.json";

    String CURRENT_GAME_FILE   = "current_game.json";

    String SETUP_GAME_FILE     = "setup_game.json";

    void connectGameRecorder();

    void disconnectGameRecorder(boolean exiting);

    void loadRecordedGames();

    List<RecordedGameService> getRecordedGameServiceList();

    RecordedGameService getRecordedGameService(long gameDate);

    Set<String> getRecordedLeagues();

    Set<String> getRecordedDivisions();

    void deleteRecordedGame(long gameDate);

    void deleteAllRecordedGames();

    boolean hasCurrentGame();

    GameService loadCurrentGame();

    void saveCurrentGame();

    void deleteCurrentGame();

    boolean hasSetupGame();

    GameService loadSetupGame();

    void saveSetupGame(GameService gameService);

    void deleteSetupGame();

    void syncGamesOnline();

    void syncGamesOnline(DataSynchronizationListener listener);

    boolean isGameIndexed(long gameDate);

    void toggleGameIndexed(long gameDate);

    void getGameFromCode(int code, AsyncGameRequestListener listener);

    void getUserGame(long id, AsyncGameRequestListener listener);

    void getUserScheduledGames(AsyncGameRequestListener listener);
    
    void scheduleUserGameOnline(GameDescription gameDescription, DataSynchronizationListener listener);
}
