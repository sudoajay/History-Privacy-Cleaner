package com.sudoajay.historycachecleaner.activity.main.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sudoajay.historycachecleaner.activity.app.database.AppRoomDatabase

@Database(entities = [Cache::class], version = 1 , exportSchema = false)
abstract class CacheRoomDatabase : RoomDatabase() {

    abstract fun cacheDao():CacheDao


    companion object {
        @Volatile
        private var INSTANCE: CacheRoomDatabase? = null

        fun getDatabase(
            context: Context
        ): CacheRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CacheRoomDatabase::class.java,
                    "cacheItem_database"
                )

                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}