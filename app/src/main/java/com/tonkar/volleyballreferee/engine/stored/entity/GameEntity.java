package com.tonkar.volleyballreferee.engine.stored.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.tonkar.volleyballreferee.engine.stored.dto.ApiGameSummary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "games")
public class GameEntity extends ApiGameSummary {

    @NonNull
    @ColumnInfo(name = "content")
    private String content;

    public GameEntity() {
        super();
        content = "";
    }

}

