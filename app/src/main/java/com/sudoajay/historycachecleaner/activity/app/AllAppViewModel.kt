
package com.sudoajay.historycachecleaner.activity.app

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import com.sudoajay.historycachecleaner.activity.app.database.App
import com.sudoajay.historycachecleaner.activity.app.database.AppDao
import com.sudoajay.historycachecleaner.activity.app.database.AppRepository
import com.sudoajay.historycachecleaner.activity.app.database.AppRoomDatabase
import com.sudoajay.historycachecleaner.helper.CustomToast
import com.sudoajay.historyprivacycleaner.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlin.system.measureTimeMillis


class AllAppViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "AllActivityView"
    private var loadApps: LoadApps
    private var _application = application
    var appRepository: AppRepository
    private var appDao: AppDao =
        AppRoomDatabase.getDatabase(_application.applicationContext).appDao()

    var hideProgress: MutableLiveData<Boolean> = MutableLiveData()

    val filterChanges: MutableLiveData<String> = MutableLiveData()
    var stopObservingData: Boolean = false
    var isCacheUpdateInDatBase = false
    private var job: Job? = null
    lateinit var appList:Flow<PagingData<App>>

    init {
        //        Creating Object and Initialization
        appRepository = AppRepository(application, appDao)
        loadApps = LoadApps(_application.applicationContext, appRepository,this)
        loadHideProgress()

        fetchDataFromDataBase()

        filterChanges()

        databaseConfiguration()


    }

    fun filterChanges(filter: String = _application.getString(R.string.filter_changes_text)) {
        filterChanges.value = filter
    }


    private fun loadHideProgress() {
        hideProgress.value = false
    }

    fun fetchDataFromDataBase(){

        appList=  appRepository.handleFilterChanges(filterChanges.value.toString()).cachedIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }


    private fun databaseConfiguration() {
      job =  CoroutineScope(Dispatchers.IO).launch {
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

        val oneMoreAnotherTime = measureTimeMillis {
            withContext(Dispatchers.Default) { thirdTaskIsDataBaseEmpty() }
        }
        Log.e(TAG , "Completed in Second $oneMoreAnotherTime ms")

    }

    private suspend fun firstTaskAppList() {
        if (appRepository.getPackageList().isEmpty()) {
            stopObservingData = true
            loadApps.searchInstalledApps("Empty DataBase")
            stopObservingData = false
            //        It will delay the process for 1 sec
            delay(1000)
        }
        filterChanges.postValue(_application.getString(R.string.filter_changes_text))
        hideProgress.postValue(true)
    }

    private suspend fun secondTaskSetAppSize() {
        loadApps.searchInstalledApps("Not Empty DataBase")
        isCacheUpdateInDatBase = true
        stopObservingData = false
        filterChanges.postValue(_application.getString(R.string.filter_changes_text))

    }

    private suspend fun thirdTaskIsDataBaseEmpty(){
        if (appRepository.getPackageList().isEmpty()){
            withContext(Dispatchers.Main){
                CustomToast.toastIt(
                    _application,
                    _application.getString(R.string.alert_dialog_no_cache_app)
                )
            }
        }

    }

}