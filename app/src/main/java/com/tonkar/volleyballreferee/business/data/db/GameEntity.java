package com.tonkar.volleyballreferee.business.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import com.tonkar.volleyballreferee.api.ApiGameDescription;
import lombok.Getter;
import lombok.Setter;

@Entity(tableName = "games")
@Getter @Setter
public class GameEntity extends ApiGameDescription {

    @NonNull
    @ColumnInfo(name = "content")
    private String content;

    public GameEntity() {
        super();
        content = "";
    }

}
