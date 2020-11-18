package com.sudoajay.historycachecleaner.activity.main.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sudoajay.historycachecleaner.activity.app.database.App
import com.sudoajay.historycachecleaner.activity.app.database.AppDao
import com.sudoajay.historycachecleaner.activity.app.database.AppRoomDatabase

@Database(entities = [App::class], version = 1 , exportSchema = false)
abstract class CacheRoomDatabase : RoomDatabase() {

    abstract fun cacheDao(): CacheDao


    companion object {
        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun getDatabase(
            context: Context
        ): AppRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppRoomDatabase::class.java,
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