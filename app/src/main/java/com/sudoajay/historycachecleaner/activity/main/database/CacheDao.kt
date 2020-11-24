package com.sudoajay.historycachecleaner.activity.main.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sudoajay.historycachecleaner.activity.app.database.App


@Dao
interface CacheDao {


    @Query("Select * FROM CacheTable ")
    fun getCacheList(): LiveData<List<Cache>>

    @Query("DELETE FROM CacheTable")
    suspend fun deleteAll()


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(cache: Cache)


    @Query("Select Count(id) FROM CacheTable ")
    suspend fun getCount(): Int

    @Query("UPDATE CacheTable SET Selected = :selected  Where name = :name")
    suspend fun updateSelectedApp(selected: Boolean, name: String)

    @Query("Select * FROM CacheTable Where Selected =1 ")
    suspend fun getSelectedApp(): MutableList<Cache>


}