package com.tonkar.volleyballreferee.engine.game.timeout;

import com.tonkar.volleyballreferee.engine.stored.dto.ApiTimeout;
import com.tonkar.volleyballreferee.engine.team.TeamType;

import java.util.List;

public interface IBaseTimeout {

    int countRemainingTimeouts(TeamType teamType);

    int countRemainingTimeouts(TeamType teamType, int setIndex);

    List<ApiTimeout> getCalledTimeouts(TeamType teamType);

    List<ApiTimeout> getCalledTimeouts(TeamType teamType, int setIndex);

}
