package com.example.user.ex_inmobiles;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.user.ex_inmobiles.dao.DaoAccess;
import com.example.user.ex_inmobiles.entity.ImageEntity;

/**
 * Created by user on 1/3/2018.
 */

@Database(entities = {ImageEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DaoAccess daoAccess();
}