package com.sudoajay.historycachecleaner.activity.progress

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sudoajay.circleloadinganimation.AnimatedCircleLoadingView
import com.sudoajay.historycachecleaner.activity.app.AllApp
import com.sudoajay.historycachecleaner.activity.app.LoadApps
import com.sudoajay.historycachecleaner.activity.app.database.AppDao
import com.sudoajay.historycachecleaner.activity.app.database.AppRepository
import com.sudoajay.historycachecleaner.activity.app.database.AppRoomDatabase
import com.sudoajay.historycachecleaner.activity.main.MainActivity
import com.sudoajay.historycachecleaner.activity.main.database.CacheDao
import com.sudoajay.historycachecleaner.activity.main.database.CacheRepository
import com.sudoajay.historycachecleaner.activity.main.database.CacheRoomDatabase
import com.sudoajay.historycachecleaner.helper.CustomToast
import com.sudoajay.historycachecleaner.helper.FileSize
import com.sudoajay.historycachecleaner.helper.root.RootManager
import com.sudoajay.historycachecleaner.helper.root.RootState
import com.sudoajay.historyprivacycleaner.R
import kotlinx.android.synthetic.main.activity_progress.*
import kotlinx.coroutines.*

class ProgressActivity : AppCompatActivity() {
    private var animatedCircleLoadingView: AnimatedCircleLoadingView? = null
    private var TAG = "ProgressActivityTAG"
    private var totalCacheSize = 0L
    private var stopProgress = false
    private lateinit var appDao: AppDao
    private lateinit var appRepository: AppRepository
    private lateinit var loadApps: LoadApps
    private lateinit var rootManager: RootManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)
        val action = intent.action.toString()

        animatedCircleLoadingView = circle_loading_view
        startLoading()

        appDao = AppRoomDatabase.getDatabase(applicationContext).appDao()
        appRepository = AppRepository(applicationContext, appDao)
//        loadApps = LoadApps(applicationContext, appRepository)
        rootManager = RootManager(applicationContext)

        if (action == MainActivity.homeShortcutId)
            deleteCacheItem()
        else if (action == MainActivity.allAppId) {
            CoroutineScope(Dispatchers.IO).launch {
                deleteAppCacheData()
            }
        }
        val closeProgress = imageView_closeProgress
        closeProgress.setOnClickListener {
            it.visibility = View.GONE
            animatedCircleLoadingView!!.stopFailure()
        }


//         After Progress finished code
        animatedCircleLoadingView!!.progressFinished.observe(this, {
            if (action == MainActivity.homeShortcutId) {
                startActivity(Intent(this, MainActivity::class.java))
                if (it)
                    CustomToast.toastIt(
                        this,
                        getString(R.string.you_have_saved_cache_item_text)
                    )
            } else if (action == MainActivity.allAppId) {
                startActivity(Intent(this, AllApp::class.java))
                if (it)
                    CustomToast.toastIt(
                        this,
                        getString(
                            R.string.you_have_saved_app_cache_text,
                            FileSize.convertIt(totalCacheSize)
                        )
                    )
            }
        })


    }


    private  fun startLoading() {
        animatedCircleLoadingView!!.startDeterminate()
    }

    private suspend fun deleteAppCacheData(showPercent: Boolean = true) {
        val selectedList = appRepository.getSelectedApp()
        val rootState: RootState = rootManager.checkRootPermission()!!
        selectedList.forEachIndexed forEach@{ index, app ->
            if (stopProgress) return@forEach
            totalCacheSize += app.cacheSize
            if (rootState == RootState.HAVE_ROOT)
                rootManager.removeCacheFolderRoot(app)
            else rootManager.removeCacheFolderUnRoot(app)
            if (showPercent) {
                withContext(Dispatchers.Main) {
                    changePercent(((index + 1) * 100) / selectedList.size)
                }
            }
        }
    }

    private fun deleteCacheItem() {
        val cacheDao: CacheDao =
            CacheRoomDatabase.getDatabase(applicationContext).cacheDao()
        val cacheRepository = CacheRepository(cacheDao)
        CoroutineScope(Dispatchers.IO).launch {
            val selectedList = cacheRepository.getSelectedApp()
            selectedList.forEachIndexed forEach@{ index, it ->
                delay(500)
                withContext(Dispatchers.Main) {
                    changePercent(((index) * 100) / selectedList.size)
                }
                when (it.name) {
                    getString(R.string.all_app_cache_text) -> {
                        withContext(Dispatchers.IO) {
                            loadApps.searchInstalledApps()
                        }
                        deleteAppCacheData(false)
                    }

                    getString(R.string.download_folder_text) -> {
                        if (rootManager.checkRootPermission()!! == RootState.HAVE_ROOT)
                            rootManager.removeDownloadsFolderRoot()
                        else
                            rootManager.removeDownloadsFolderUnRoot()
                    }
                    getString(R.string.browser_default_only_text) -> {
                        if (rootManager.checkRootPermission()!! == RootState.HAVE_ROOT)
                            rootManager.removeBrowserDataRoot()
                    }
                    else -> clearClipBoard()
                }
            }
            withContext(Dispatchers.Main) {
                changePercent(100)
            }
        }
    }



    private fun clearClipBoard() {
        val clipBoardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("", "")
        clipBoardManager.setPrimaryClip(clipData)

    }

    private fun changePercent(percent: Int) {
        animatedCircleLoadingView!!.setPercent(percent)
    }

    override fun onBackPressed() {
        stopProgress = true
        super.onBackPressed()
    }
}