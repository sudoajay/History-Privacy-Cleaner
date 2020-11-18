package com.sudoajay.historycachecleaner.activity.main.database

import androidx.room.Dao
import androidx.room.Query


@Dao
interface CacheDao {

    @Query("DELETE FROM AppTable")
    suspend fun deleteAll()


}