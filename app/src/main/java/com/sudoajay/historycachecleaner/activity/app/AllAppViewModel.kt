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
import com.sudoajay.historycachecleaner.activity.main.database.CacheRepository
import com.sudoajay.historycachecleaner.activity.app.database.AppRoomDatabase
import com.sudoajay.historycachecleaner.helper.root.RootManager
import com.sudoajay.historyprivacycleaner.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllAppViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "AllActivityView ModelTag"
    private var loadApps: LoadApps
    private var _application = application
    var appRepository: AppRepository
    var  rootManager: RootManager = RootManager(application)
    private var appDao: AppDao =
        AppRoomDatabase.getDatabase(_application.applicationContext).appDao()

    var hideProgress: MutableLiveData<Boolean>? = null

    private val filterChanges: MutableLiveData<String> = MutableLiveData()


    var appList: LiveData<PagedList<App>>? = null

    init {
        //        Creating Object and Initialization
        appRepository = AppRepository(application,appDao)
        loadApps = LoadApps(_application.applicationContext, appRepository)

        getHideProgress()
        appList = Transformations.switchMap(filterChanges) {
            appRepository.handleFilterChanges(it)
        }
        filterChanges()

        databaseConfiguration()

    }

    fun filterChanges(filter: String = _application.getString(R.string.filter_changes_text)) {
        Log.e(TAG , "Filter Change - $filterChanges")
        filterChanges.value = filter
    }
    fun onRefresh() {

        appList!!.value!!.dataSource.invalidate()
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
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                loadApps.searchInstalledApps()
            }
            filterChanges.postValue(_application.getString(R.string.filter_changes_text))

        }

    }


}