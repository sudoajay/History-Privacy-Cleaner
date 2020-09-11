package com.sudoajay.historyprivacycleaner.activity.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var loadApps: LoadApps
    private var _application = application
    var appRepository: AppRepository
    var  rootManager: RootManager = RootManager(this, application)
    private var appDao: AppDao =
        AppRoomDatabase.getDatabase(_application.applicationContext).appDao()

    var successfullyAppRemoved:MutableLiveData<Boolean> = MutableLiveData()
    var hideProgress: MutableLiveData<Boolean>? = null

    private val filterChanges: MutableLiveData<String> = MutableLiveData()


    var appList: LiveData<PagedList<App>>? = null

    init {
        //        Creating Object and Initialization
        appRepository = AppRepository(_application.applicationContext, appDao)
        loadApps = LoadApps(_application.applicationContext, appRepository)

        getHideProgress()
        appList = Transformations.switchMap(filterChanges) {
            appRepository.handleFilterChanges(it)
        }
        filterChanges()

        databaseConfiguration()

    }


    fun filterChanges(filter: String = _application.getString(R.string.filter_changes_text)) {
        filterChanges.value = filter
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

    fun onRefresh() {
        databaseConfiguration()
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

    fun checkRootPermission(): RootState? {
        val hasRootedPermission: Boolean = rootManager.hasRootedPermission()
        if (hasRootedPermission) return RootState.HAVE_ROOT
        val wasRooted: Boolean = rootManager.wasRooted()
        return if (wasRooted) RootState.BE_ROOT else RootState.NO_ROOT
    }


}