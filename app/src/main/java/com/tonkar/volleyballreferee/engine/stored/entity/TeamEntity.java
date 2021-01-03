package com.tonkar.volleyballreferee.engine.stored.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.tonkar.volleyballreferee.engine.stored.dto.ApiTeamSummary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "teams")
public class TeamEntity extends ApiTeamSummary {

    @NonNull
    @ColumnInfo(name = "content")
    private String content;

    public TeamEntity() {
        super();
        content = "";
    }

}
