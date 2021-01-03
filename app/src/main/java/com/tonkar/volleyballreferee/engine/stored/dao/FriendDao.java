package com.tonkar.volleyballreferee.engine.stored.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.tonkar.volleyballreferee.engine.stored.entity.FriendEntity;

import java.util.List;

@Dao
public interface FriendDao {

    @Query("SELECT * FROM friends")
    List<FriendEntity> listFriends();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FriendEntity friendEntity);

    @Query("DELETE FROM friends")
    void deleteAll();

    @Query("DELETE FROM friends WHERE id = :id")
    void deleteById(String id);

    @Query("SELECT COUNT(*) FROM friends")
    int count();

}
