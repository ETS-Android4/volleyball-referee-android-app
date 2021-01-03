package com.tonkar.volleyballreferee.engine.stored.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.tonkar.volleyballreferee.engine.stored.dto.ApiRulesSummary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "rules")
public class RulesEntity extends ApiRulesSummary {

    @NonNull
    @ColumnInfo(name = "content")
    private String content;

    public RulesEntity() {
        super();
        content = "";
    }

}
