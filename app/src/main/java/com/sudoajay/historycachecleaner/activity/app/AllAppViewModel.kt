
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
import com.sudoajay.historyprivacycleaner.R
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis


class AllAppViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "AllActivityView"
    private var loadApps: LoadApps
    private var _application = application
    var appRepository: AppRepository
    private var appDao: AppDao =
        AppRoomDatabase.getDatabase(_application.applicationContext).appDao()

    var hideProgress: MutableLiveData<Boolean> = MutableLiveData()

    private val filterChanges: MutableLiveData<String> = MutableLiveData()
    var stopObservingData: Boolean = false
    var isCacheUpdateInDatBase = false

    var appList: LiveData<PagedList<App>>? = null

    init {
        //        Creating Object and Initialization
        appRepository = AppRepository(application, appDao)
        loadApps = LoadApps(_application.applicationContext, appRepository,this)
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
            asyncTask()
        }
    }

    private fun asyncTask() = runBlocking {
        val time = measureTimeMillis {
            withContext(Dispatchers.Default) { firstTaskAppList() }
        }
        Log.e(TAG , "Completed in first Task $time ms")
        val anotherTime = measureTimeMillis {
            withContext(Dispatchers.Default) { secondTaskSetAppSize() }
        }
        Log.e(TAG , "Completed in Second $anotherTime ms")
    }

    private suspend fun firstTaskAppList() {
        val packageList: List<String> = appRepository.getPackageList()
        if (packageList.isEmpty()) {
            stopObservingData = true
            loadApps.searchInstalledApps("Empty DataBase")
            stopObservingData = false
            //        It will delay the process for 1 sec
            delay(1000)
            onRefresh()
        }
    }

    private suspend fun secondTaskSetAppSize() {
        loadApps.searchInstalledApps("Not Empty DataBase")
        isCacheUpdateInDatBase = true
        stopObservingData = false
        onRefresh()
    }

}