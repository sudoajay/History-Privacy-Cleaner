package com.sudoajay.historycachecleaner.activity.main

import android.content.Context
import com.sudoajay.historycachecleaner.activity.main.database.Cache
import com.sudoajay.historycachecleaner.activity.main.database.CacheRepository
import com.sudoajay.historyprivacycleaner.R


class LoadCache(private var context: Context, private var cacheRepository: CacheRepository){

    suspend fun fillDefaultData() {
        val cacheItem: MutableList<Cache> = ArrayList()
        cacheItem.add(
            Cache(
                null, context.getString(R.string.all_app_cache_text), "",
                isSelected = true
            )
        )

        cacheItem.add(
            Cache(
                null, context.getString(R.string.download_folder_text), "",
                isSelected = true
            )
        )

        cacheItem.add(
            Cache(
                null, context.getString(R.string.browser_default_only_text), "",
                isSelected = true
            )
        )

        cacheItem.add(
            Cache(
                null, context.getString(R.string.clipboard_text), "",
                isSelected = true
            )
        )

        // Fill in the data base
        for (i in cacheItem) {
            cacheRepository.insert(i)
        }
    }
}