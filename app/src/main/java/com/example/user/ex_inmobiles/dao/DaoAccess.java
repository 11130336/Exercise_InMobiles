package com.example.user.ex_inmobiles.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.user.ex_inmobiles.entity.ImageEntity;

import java.util.List;

/**
 * Created by user on 1/3/2018.
 */

@Dao
public interface DaoAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImages(ImageEntity... images);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOnlySingleImage(ImageEntity image);

    @Query("SELECT * FROM images")
    LiveData<List<ImageEntity>> fetchAllData();

}
