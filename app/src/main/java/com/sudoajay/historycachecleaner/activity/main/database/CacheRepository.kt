package com.sudoajay.historycachecleaner.activity.main.database

import androidx.lifecycle.LiveData


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
}