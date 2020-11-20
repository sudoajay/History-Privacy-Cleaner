package com.sudoajay.historycachecleaner.activity.progress

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sudoajay.circleloadinganimation.AnimatedCircleLoadingView
import com.sudoajay.historycachecleaner.activity.app.AllApp
import com.sudoajay.historycachecleaner.activity.main.MainActivity
import com.sudoajay.historycachecleaner.activity.app.database.AppDao
import com.sudoajay.historycachecleaner.activity.main.database.CacheRepository
import com.sudoajay.historycachecleaner.activity.app.database.AppRoomDatabase
import com.sudoajay.historycachecleaner.helper.root.RootManager
import com.sudoajay.historycachecleaner.helper.root.RootState
import com.sudoajay.historycachecleaner.helper.CustomToast
import com.sudoajay.historycachecleaner.helper.FileSize
import com.sudoajay.historyprivacycleaner.R
import kotlinx.android.synthetic.main.activity_progress.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProgressActivity : AppCompatActivity() {
    private var animatedCircleLoadingView: AnimatedCircleLoadingView? = null
    private var TAG = "ProgressActivityTAG"
    private var totalCacheSize = 0L
    private var stopProgress = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)
        val action = intent.action.toString()

        animatedCircleLoadingView = circle_loading_view
        startLoading()


        if (action == MainActivity.appCacheDataId)
            deleteAppCacheData()


        val closeProgress =imageView_closeProgress
        closeProgress.setOnClickListener {
            it.visibility= View.GONE
            animatedCircleLoadingView!!.stopFailure()
        }


//         After Progress finished code
        animatedCircleLoadingView!!.progressFinished.observe(this, {
            startActivity(Intent(this, AllApp::class.java))
            if (it)
                CustomToast.toastIt(
                    this,
                    getString(R.string.you_have_saved_text, FileSize.convertIt(totalCacheSize))
                )

        })
    }

    private fun startLoading() {
        animatedCircleLoadingView!!.startDeterminate()
    }

    private fun deleteAppCacheData() {

        val rootManager = RootManager(applicationContext)
        val appDao: AppDao =
            AppRoomDatabase.getDatabase(applicationContext).appDao()
        val appRepository = CacheRepository(appDao)
        CoroutineScope(Dispatchers.IO).launch {
            val selectedList = appRepository.getSelectedApp()
            val rootState: RootState = rootManager.checkRootPermission()!!
            selectedList.forEachIndexed forEach@{ index, app ->
                if (stopProgress) return@forEach
                totalCacheSize += app.cacheSize
                if (rootState == RootState.HAVE_ROOT)
                    rootManager.removeCacheFolderRoot(app)
                else rootManager.removeCacheFolderUnRoot(app)

                withContext(Dispatchers.Main) {
                    changePercent(((index + 1) * 100) / selectedList.size)
                }
            }
        }


    }

    private fun changePercent(percent: Int) {
        animatedCircleLoadingView!!.setPercent(percent)
    }

    override fun onBackPressed() {
        stopProgress = true
        super.onBackPressed()
    }
}