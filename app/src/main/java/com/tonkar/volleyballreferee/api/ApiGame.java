package com.tonkar.volleyballreferee.api;

import com.google.gson.annotations.SerializedName;
import com.tonkar.volleyballreferee.interfaces.GameStatus;
import com.tonkar.volleyballreferee.interfaces.GameType;
import com.tonkar.volleyballreferee.interfaces.UsageType;
import com.tonkar.volleyballreferee.interfaces.team.GenderType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter @EqualsAndHashCode
public class ApiGame {

    @SerializedName("id")
    private String            id;
    @SerializedName("createdBy")
    private String            createdBy;
    @SerializedName("createdAt")
    private long              createdAt;
    @SerializedName("updatedAt")
    private long              updatedAt;
    @SerializedName("scheduledAt")
    private long              scheduledAt;
    @SerializedName("refereedBy")
    private String            refereedBy;
    @SerializedName("refereeName")
    private String            refereeName;
    @SerializedName("kind")
    private GameType          kind;
    @SerializedName("gender")
    private GenderType        gender;
    @SerializedName("usage")
    private UsageType         usage;
    @SerializedName("status")
    private GameStatus        status;
    @SerializedName("indexed")
    private boolean           indexed;
    @SerializedName("leagueId")
    private String            leagueId;
    @SerializedName("leagueName")
    private String            leagueName;
    @SerializedName("divisionName")
    private String            divisionName;
    @SerializedName("homeTeam")
    private ApiTeam           homeTeam;
    @SerializedName("guestTeam")
    private ApiTeam           guestTeam;
    @SerializedName("homeSets")
    private int               homeSets;
    @SerializedName("guestSets")
    private int               guestSets;
    @SerializedName("sets")
    private List<ApiSet>      sets;
    @SerializedName("homeCards")
    private List<ApiSanction> homeCards;
    @SerializedName("guestCards")
    private List<ApiSanction> guestCards;
    @SerializedName("rules")
    private ApiRules          rules;
    @SerializedName("score")
    private String            score;

    public ApiGame() {
        id = UUID.randomUUID().toString();
        createdBy = Authentication.VBR_USER_ID;
        createdAt = 0L;
        updatedAt = 0L;
        scheduledAt = 0L;
        refereedBy = Authentication.VBR_USER_ID;
        refereeName = "";
        kind = GameType.INDOOR;
        gender = GenderType.MIXED;
        usage = UsageType.NORMAL;
        status = GameStatus.SCHEDULED;
        indexed = true;
        leagueId = null;
        leagueName = "";
        divisionName = "";
        homeTeam = null;
        guestTeam = null;
        homeSets = 0;
        guestSets = 0;
        sets = new ArrayList<>();
        homeCards = new ArrayList<>();
        guestCards = new ArrayList<>();
        rules = null;
    }

}
