package com.tonkar.volleyballreferee.engine.stored.entity;

import androidx.room.Entity;

import com.tonkar.volleyballreferee.engine.stored.dto.ApiFriend;

@Entity(tableName = "friends")
public class FriendEntity extends ApiFriend {

    public FriendEntity() {
        super();
    }
}
