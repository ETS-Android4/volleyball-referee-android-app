package com.tonkar.volleyballreferee.engine.stored;

import com.tonkar.volleyballreferee.engine.game.IBaseGeneral;
import com.tonkar.volleyballreferee.engine.game.IBaseTime;
import com.tonkar.volleyballreferee.engine.game.sanction.IBaseSanction;
import com.tonkar.volleyballreferee.engine.game.score.IBaseScore;
import com.tonkar.volleyballreferee.engine.game.timeout.IBaseTimeout;
import com.tonkar.volleyballreferee.engine.stored.dto.ApiSanction;
import com.tonkar.volleyballreferee.engine.stored.dto.ApiSubstitution;
import com.tonkar.volleyballreferee.engine.stored.dto.ApiTimeout;
import com.tonkar.volleyballreferee.engine.team.IBaseTeam;
import com.tonkar.volleyballreferee.engine.team.TeamType;

import java.util.List;

public interface IStoredGame extends IBaseGeneral, IBaseScore, IBaseTeam, IBaseTimeout, IBaseSanction, IBaseTime {

    int getGameCaptain(TeamType teamType, int setIndex);

    List<ApiTimeout> getTimeoutsIfExist(TeamType teamType, int setIndex, int hPoints, int gPoints);

    List<ApiSubstitution> getSubstitutionsIfExist(TeamType teamType, int setIndex, int hPoints, int gPoints);

    List<ApiSanction> getSanctionsIfExist(TeamType teamType, int setIndex, int hPoints, int gPoints);

}
