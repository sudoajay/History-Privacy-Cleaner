package com.sudoajay.historycachecleaner.activity.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {


    private var _application = application

    var hideProgress: MutableLiveData<Boolean>? = null



    init {

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
        getHideProgress()


    }


}