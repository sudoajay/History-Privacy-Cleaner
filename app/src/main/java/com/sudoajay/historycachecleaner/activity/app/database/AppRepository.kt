package com.sudoajay.historycachecleaner.activity.app.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.toLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.sudoajay.historycachecleaner.activity.BaseActivity
import com.sudoajay.historyprivacycleaner.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AppRepository(private val context: Context, private val appDao: AppDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    lateinit var app: DataSource.Factory<Int, App>
    lateinit var id: List<Int>

    fun handleFilterChanges(filter: String): LiveData<PagedList<App>> {
        if (filter == context.getString(R.string.filter_changes_text)) {
            //         Sorting Data in Alpha or Install date
            val getOrderBy =
                BaseActivity.orderBy.value ?: context.getString(R.string.menu_alphabetical_order)

//         Is System App Show
            val isSystemApp = if (BaseActivity.systemApps.value == true) 1 else 2
//         Is User App Show

            val isUserApp = if (BaseActivity.userApps.value == true) 1 else 2

            //         get Changes If the Selected App
            modifyDatabase()

            app = when (getOrderBy) {
                context.getString(R.string.menu_alphabetical_order) -> {
                    appDao.getSortByAlpha(isSystemApp, isUserApp)
                }
                context.getString(R.string.menu_installation_date) -> {
                    appDao.getSortByDate(isSystemApp, isUserApp)
                }
                else -> {
                    appDao.getSortBySize(isSystemApp, isUserApp)
                }
            }
            return app.toLiveData(
                PagedList.Config.Builder()
                    .setPageSize(50)
                    .setEnablePlaceholders(false) //
                    .build()
            )
        } else {

            val value = "%$filter%"


            return appDao.searchItem(value).toLiveData(
                PagedList.Config.Builder()
                    .setPageSize(10) //
                    .setEnablePlaceholders(true) //
                    .build()
            )

        }
    }


    private fun modifyDatabase() {

        //        Option Selected

        when (BaseActivity.selectOption.value ?: context.getString(R.string.menu_custom_app)) {
            context.getString(R.string.menu_no_apps_trans) ->
                getId(1, SimpleSQLiteQuery("Select id From AppTable Where Selected = '1'"))
            context.getString(R.string.menu_all_apps_trans) ->
                getId(2, SimpleSQLiteQuery("Select id From AppTable Where Selected = '0'"))
            context.getString(R.string.menu_only_user_apps_trans) ->
                getId(3, SimpleSQLiteQuery("Select id From AppTable Where User_App = '1'"))
            context.getString(R.string.menu_only_system_apps_trans) ->
                getId(4, SimpleSQLiteQuery("Select id From AppTable Where System_App = '1'"))
        }
    }


    private fun getId(type: Int, query: SimpleSQLiteQuery) {

        var value: Boolean
        CoroutineScope(Dispatchers.Default).launch {
            value = when (type) {
                1 -> false
                2 -> true
                3, 4 -> {
                    withContext(Dispatchers.Default) {
                        id =
                            appDao.getIdViaQuery(SimpleSQLiteQuery("Select id From AppTable Where Selected = '1'"))
                        updateTheList(false, id)
                    }
                    true
                }
                else -> false
            }

            withContext(Dispatchers.Default) {
                    id = appDao.getIdViaQuery(query)
            }
            withContext(Dispatchers.Default) {
                updateTheList(value, id)
            }

        }
    }

    private suspend fun updateTheList(value: Boolean, id: List<Int>) {
        for (i in id) {
            appDao.updateSelectedById(value, i)
        }
    }

    suspend fun updateAllCacheSize(packageList: List<String>) {
        for (packageName in packageList) {
            appDao.updateCacheSizeByPackage(packageName, -1)
        }
    }


    suspend fun insert(app: App) {
        appDao.insert(app)
    }

    suspend fun getAppFromId(id: Int): App {
        return appDao.getAppFromId(id)
    }


    suspend fun updateCacheSizeAndInstalledByPackage(packageName: String){
        appDao.updateCacheSizeAndInstalledByPackage(packageName)
    }

    suspend fun updateCacheSizeByPackage(packageName: String, cacheSize : Long){
        appDao.updateCacheSizeByPackage(packageName,cacheSize)
    }

    suspend fun setDefaultValueInstall() {
        appDao.setDefaultValueInstall()
    }

    suspend fun removeUninstallAppFromDB() {
        for (i in appDao.getUninstallList()) {
            appDao.deleteRow(i)
        }
    }

    fun deleteRowFromPackage(packageName: String) {
        appDao.deleteRowFromPackage(packageName)
    }

    suspend fun getPackageList(): List<String> {
        return appDao.getPackageList()
    }


    suspend fun getSelectedApp(): MutableList<App> {
        return appDao.getSelectedApp()
    }

    suspend fun updateSelectedApp(selected: Boolean, packageName: String) {
        appDao.updateSelectedApp(selected, packageName)
    }


}