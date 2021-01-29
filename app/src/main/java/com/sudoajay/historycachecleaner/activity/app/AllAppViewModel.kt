
package com.sudoajay.historycachecleaner.activity.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import com.sudoajay.historycachecleaner.activity.app.database.App
import com.sudoajay.historycachecleaner.activity.app.database.AppDao
import com.sudoajay.historycachecleaner.activity.app.database.AppRepository
import com.sudoajay.historycachecleaner.activity.app.database.AppRoomDatabase
import com.sudoajay.historycachecleaner.helper.root.RootManager
import com.sudoajay.historyprivacycleaner.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AllAppViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "AllActivityView"
    private var loadApps: LoadApps
    private var _application = application
    var appRepository: AppRepository
    private var appDao: AppDao =
        AppRoomDatabase.getDatabase(_application.applicationContext).appDao()

    var hideProgress: MutableLiveData<Boolean> = MutableLiveData()

    val filterChanges: MutableLiveData<String> = MutableLiveData()
    var stopObservingData:Boolean = false


    var appList: LiveData<PagedList<App>>? = null

    init {
        //        Creating Object and Initialization
        appRepository = AppRepository(application, appDao)
        loadApps = LoadApps(_application.applicationContext, appRepository)
        loadHideProgress()
        appList = Transformations.switchMap(filterChanges) {
            appRepository.handleFilterChanges(it)
        }
        filterChanges()

        databaseConfiguration()
    }
    fun filterChanges(filter: String = _application.getString(R.string.filter_changes_text)) {
        filterChanges.value = filter
    }
    fun onRefresh() {
        appList!!.value!!.dataSource.invalidate()
    }

    private fun loadHideProgress() {
        hideProgress.value = false
    }
    private fun databaseConfiguration() {

        CoroutineScope(Dispatchers.IO).launch {
            delay(500)
            stopObservingData = true
            loadApps.searchInstalledApps()
            onRefresh()
            stopObservingData = false
        }

    }


}