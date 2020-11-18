package com.sudoajay.historycachecleaner.activity.main

import com.sudoajay.historycachecleaner.activity.main.database.Cache

class LoadCacheItem(){

    suspend fun fillDefaultData(){
        val cacheItem: MutableList<Cache> = ArrayList()
        cacheItem.add(Cache(null,"Apps Cache" , "", true))

        cacheItem.add(Cache(null,"Browser (Default Only)" , "", true))

        cacheItem.add(Cache(null,"Clipboard" , "", true))
    }
}