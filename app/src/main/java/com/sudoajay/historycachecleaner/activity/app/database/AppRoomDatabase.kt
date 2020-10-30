package com.sudoajay.historycachecleaner.activity.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [App::class], version = 1 , exportSchema = false)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao


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
                    "app_database"
                )

                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}