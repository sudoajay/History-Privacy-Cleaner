package com.sudoajay.historycachecleaner.activity.main.database


class CacheRepository(private val cacheDao: CacheDao) {


    suspend fun insert(cache: Cache) {
        cacheDao.insert(cache)
    }

    suspend fun getCount(): Int {
        return cacheDao.getCount()
    }
}