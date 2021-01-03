package com.tonkar.volleyballreferee.engine.stored.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.tonkar.volleyballreferee.engine.stored.dto.ApiLeagueSummary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "leagues")
public class LeagueEntity extends ApiLeagueSummary {

    @NonNull
    @ColumnInfo(name = "content")
    private String content;

    public LeagueEntity() {
        super();
        content = "";
    }

}
