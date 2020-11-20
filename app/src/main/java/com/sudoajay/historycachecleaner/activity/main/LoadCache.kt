package com.sudoajay.historycachecleaner.activity.main

import android.content.Context
import com.sudoajay.historycachecleaner.activity.main.database.Cache
import com.sudoajay.historycachecleaner.activity.main.database.CacheRepository

class LoadCache(private var context: Context, private var cacheRepository: CacheRepository){

    suspend fun fillDefaultData(){
        val cacheItem: MutableList<Cache> = ArrayList()
        cacheItem.add(Cache(null,"Apps Cache" , "", true))

        cacheItem.add(Cache(null,"Browser (Default Only)" , "", true))

        cacheItem.add(Cache(null,"Clipboard" , "", true))

        // Fill in the data base
        for(i in cacheItem){
            cacheRepository.insert(i)
        }
    }
}