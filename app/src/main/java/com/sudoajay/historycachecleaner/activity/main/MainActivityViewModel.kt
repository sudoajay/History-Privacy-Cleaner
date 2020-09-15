package com.sudoajay.historycachecleaner.activity.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sudoajay.historycachecleaner.activity.main.root.RootManager
import com.sudoajay.historycachecleaner.activity.main.root.RootState

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {


    var  rootManager: RootManager = RootManager(this, application)

    init {

    }


    fun checkRootPermission(): RootState? {
        val hasRootedPermission: Boolean = rootManager.hasRootedPermission()
        if (hasRootedPermission) return RootState.HAVE_ROOT
        val wasRooted: Boolean = rootManager.wasRooted()
        return if (wasRooted) RootState.BE_ROOT else RootState.NO_ROOT
    }


}