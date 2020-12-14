package com.sudoajay.historycachecleaner.activity.settingActivity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.sudoajay.historycachecleaner.activity.app.database.AppRepository
import com.sudoajay.historycachecleaner.activity.app.database.AppRoomDatabase
import com.sudoajay.historycachecleaner.helper.root.RootManager
import com.sudoajay.historycachecleaner.helper.root.RootState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BackGroundTask(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private lateinit var rootManager: RootManager


    override fun doWork(): Result {

        // Do the work here--in this case,
        rootManager = RootManager(applicationContext)
        deleteCacheItemBackground()
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

    private fun deleteCacheItemBackground() {
        CoroutineScope(Dispatchers.IO).launch {
            deleteAppCacheData()
            if (rootManager.checkRootPermission()!! == RootState.HAVE_ROOT) {
                rootManager.removeDownloadsFolderRoot()
                rootManager.removeBrowserDataRoot()
            } else
                rootManager.removeDownloadsFolderUnRoot()

            clearClipBoard()
        }
    }


    private suspend fun deleteAppCacheData() {
        val appDao = AppRoomDatabase.getDatabase(applicationContext).appDao()
        val appRepository = AppRepository(applicationContext, appDao)
        val selectedList = appRepository.getSelectedApp()
        val rootState: RootState = rootManager.checkRootPermission()!!
        selectedList.forEachIndexed forEach@{ _, app ->
            if (rootState == RootState.HAVE_ROOT)
                rootManager.removeCacheFolderRoot(app)
            else rootManager.removeCacheFolderUnRoot(app)

        }
    }

    private fun clearClipBoard() {
        val clipBoardManager =
            applicationContext.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("", "")
        clipBoardManager.setPrimaryClip(clipData)

    }
}
