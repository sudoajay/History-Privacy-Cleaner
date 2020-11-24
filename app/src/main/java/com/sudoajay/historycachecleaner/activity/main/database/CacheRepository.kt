package com.sudoajay.historycachecleaner.activity.main.database

import androidx.lifecycle.LiveData
import com.sudoajay.historycachecleaner.activity.app.database.App


class CacheRepository(private val cacheDao: CacheDao) {


    suspend fun getCacheList() : LiveData<List<Cache>> {
        return cacheDao.getCacheList()
    }

    suspend fun insert(cache: Cache) {
        cacheDao.insert(cache)
    }

    suspend fun getCount(): Int {
        return cacheDao.getCount()
    }

    suspend fun updateSelectedApp(selected: Boolean, name: String) {
        cacheDao.updateSelectedApp(selected, name)
    }

    suspend fun getSelectedApp(): MutableList<Cache> {
        return cacheDao.getSelectedApp()
    }
}