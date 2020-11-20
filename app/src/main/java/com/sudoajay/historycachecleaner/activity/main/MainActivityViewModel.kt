package com.sudoajay.historycachecleaner.activity.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sudoajay.historycachecleaner.activity.main.database.CacheRepository
import com.sudoajay.historycachecleaner.activity.main.database.CacheRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {


    private var _application = application

    var hideProgress: MutableLiveData<Boolean>? = null

    var cacheRepository: CacheRepository

    private var dnsDao = CacheRoomDatabase.getDatabase(_application).cacheDao()
    private var loadCache:LoadCache


    init {

        //        Creating Object and Initialization
        cacheRepository = CacheRepository(dnsDao)
        loadCache = LoadCache(application.applicationContext, cacheRepository)

        getHideProgress()

        databaseConfiguration()

    }

    fun onRefresh() {
    }
    private fun getHideProgress(): LiveData<Boolean> {
        if (hideProgress == null) {
            hideProgress = MutableLiveData()
            loadHideProgress()
        }
        return hideProgress as MutableLiveData<Boolean>
    }

    private fun loadHideProgress() {
        hideProgress!!.value = true
    }

    private fun databaseConfiguration() {
        CoroutineScope(Dispatchers.IO).launch {
            if (cacheRepository.getCount() == 0)
                loadCache.fillDefaultData()
        }
    }


}