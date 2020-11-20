package com.sudoajay.historycachecleaner.activity.main.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface CacheDao {

    @Query("DELETE FROM CacheTable")
    suspend fun deleteAll()


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(cache: Cache)


    @Query("Select Count(id) FROM CacheTable ")
    suspend fun getCount(): Int

}