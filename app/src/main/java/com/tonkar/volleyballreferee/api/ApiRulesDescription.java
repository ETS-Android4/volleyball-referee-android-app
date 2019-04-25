package com.tonkar.volleyballreferee.api;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import com.tonkar.volleyballreferee.interfaces.GameType;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ApiRulesDescription {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    @SerializedName("id")
    private String   id;
    @NonNull
    @ColumnInfo(name = "createdBy")
    @SerializedName("createdBy")
    private String   createdBy;
    @ColumnInfo(name = "createdAt")
    @SerializedName("createdAt")
    private long     createdAt;
    @ColumnInfo(name = "updatedAt")
    @SerializedName("updatedAt")
    private long     updatedAt;
    @ColumnInfo(name = "synced")
    @SerializedName("synced")
    private boolean  synced;
    @NonNull
    @ColumnInfo(name = "name")
    @SerializedName("name")
    private String   name;
    @NonNull
    @ColumnInfo(name = "kind")
    @SerializedName("kind")
    private GameType kind;

    public ApiRulesDescription() {
        id = "";
        createdBy = Authentication.VBR_USER_ID;
        createdAt = 0L;
        updatedAt = 0L;
        synced = false;
        kind = GameType.INDOOR;
        name = "";
    }

}
