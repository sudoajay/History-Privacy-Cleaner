package com.sudoajay.historycachecleaner.helper.root

import android.content.Context
import android.util.Log
import com.sudoajay.historycachecleaner.activity.BaseActivity
import com.sudoajay.historycachecleaner.activity.app.database.App
import com.sudoajay.historycachecleaner.helper.storagePermission.AndroidSdCardPermission
import eu.chainfire.libsuperuser.Shell
import java.io.File
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class RootManager(var context: Context) {

    var TAG = "RootManagerTAG"
    private val SU_BINARY_DIRS = arrayOf(
        "/system/bin",
        "/system/sbin",
        "/system/xbin",
        "/vendor/bin",
        "/sbin"
    )

    private val cachePath = "cache/"
    private val codeCache = "code_cache/"
    private val downloadFolder ="Download/"


    fun hasRootedPermission(): Boolean {
        return Shell.SU.available()
    }

    fun wasRooted(): Boolean {
        var hasRooted = false
        for (path in SU_BINARY_DIRS) {
            val su = File("$path/su")
            if (su.exists()) {
                hasRooted = true
                break
            } else {
                hasRooted = false
            }
        }
        return hasRooted
    }

//    fun removeApps(appsToRemove: List<App>) {
//        CoroutineScope(Dispatchers.IO).launch {
//            var successfully = true
//            for (app in appsToRemove) {
//                if (isRootAccessAlreadyObtained(context)) {
//                    var result: Boolean
//                    if (app.isSystemApp) {
//                        result = uninstallSystemApp(app.path)
//                        if (!result) result = uninstallSystemAppAlternativeMethod(app.packageName)
//                    } else result = uninstallUserApp(app.packageName)
//                    if (!result) successfully = false
//                } else {
//                    if (app.isUserApp)
//                        uninstallUserAppUnRooted(app.packageName)
//                    else
//                        viewModel.successfullyAppRemoved.postValue(false)
//
//                }
//            }
//            if (isRootAccessAlreadyObtained(context))
//                viewModel.successfullyAppRemoved.postValue(successfully)
//
//        }
//    }

    fun getDirListForRoot(path:String) :String{
       return executeCommandSU("ls -aR $path ")
    }
    fun getFileSizeForRoot(path: String) : Long{

        val output =  executeCommandSU("du -ks $path ")
        if (output.isEmpty()) return 0
        val pattern1 = Regex("^\\d+")
        val ans : MatchResult? = pattern1.find(output)
       return ans!!.value.toLong() * 1000
    }

    fun removeDownloadsFolderUnRoot() {
//        DeleteCache.deleteWithFile(File(getInternalCachePath(context) + downloadFolder))
        Log.e(TAG, "Done Download File deleted with Un root ")
    }
    fun removeDownloadsFolderRoot() {
//        executeCommandSU( "rm -rf %s".format( AndroidExternalStoragePermission.getExternalPathFromCacheDir(context)+ downloadFolder))
//        Log.e(TAG , "rm -rf %s".format( AndroidExternalStoragePermission.getExternalPathFromCacheDir(context)+ downloadFolder))
        Log.e(TAG, "Done Download File deleted with root ")
    }

    fun removeCacheFolderUnRoot(it: App) {
//        DeleteCache.deleteWithFile(File(getInternalCachePath(context) + it.packageName + cachePath))
//        DeleteCache.deleteWithFile(File(getInternalCachePath(context) + it.packageName + codeCache))
//
//        DeleteCache.deleteWithFile(File(getExternalCachePath(context) + it.packageName + cachePath))
//
//        DeleteCache.deleteWithFile(File(getSdCardCachePath(context) + it.packageName + cachePath))

        Log.e(TAG, "Done File deleted with Un root ")
    }

    fun removeCacheFolderRoot(it: App) {
//
//        executeCommandSU("rm  -rf %s".format(getInternalCachePath(context) + it.packageName + cachePath))
//        executeCommandSU("rm  -rf %s".format(getInternalCachePath(context) + it.packageName + codeCache))
//
//        executeCommandSU("rm  -rf %s".format(getExternalCachePath(context) + it.packageName + cachePath))
//
//        executeCommandSU("rm  -rf %s".format(getSdCardCachePath(context) + it.packageName + cachePath))

        Log.e(TAG, "Done File deleted with root ")
    }

    fun removeBrowserDataRoot(){
//        executeCommandSH("adb shell pm clear com.android.browser")
        Log.e(TAG, "remove browser File root ")
    }

//    private fun uninstallSystemApp(appApk: String): Boolean {
//        executeCommandSU("mount -o rw,remount /system")
//        executeCommandSU("rm $appApk")
//        executeCommandSU("mount -o ro,remount /system")
//        return checkUninstallSuccessful(appApk)
//    }
//
//    private fun uninstallSystemAppAlternativeMethod(packageName: String): Boolean {
//        val commandOutput = executeCommandSU("pm uninstall --user 0 $packageName")
//        return checkCommandSuccesfull(commandOutput)
//    }
//
//    private fun uninstallUserApp(packageName: String): Boolean {
//        val commandOutput = executeCommandSU("pm uninstall $packageName")
//        return checkCommandSuccesfull(commandOutput)
//    }
//
//    private fun uninstallUserAppUnRooted(packageName: String) {
//        val packageURI = Uri.parse("package:$packageName")
//        val uninstallIntent = Intent(Intent.ACTION_DELETE, packageURI)
//        uninstallIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//
//        context.startActivity(uninstallIntent)
//    }

    private fun executeCommandSU(command: String): String {
        val stdout: List<String> = ArrayList()
        val stderr: List<String> = ArrayList()
        try {
            Shell.Pool.SU.run(command, stdout, stderr, true)
        } catch (e: Shell.ShellDiedException) {
            e.printStackTrace()
        }
        val stringBuilder = StringBuilder()
        for (line in stdout) {
            stringBuilder.append(line).append("\n")
        }
        return stringBuilder.toString()
    }

    private fun executeCommandSH(command: String): String {
        val stdout: List<String> = ArrayList()
        val stderr: List<String> = ArrayList()
        try {
            Shell.Pool.SH.run(command, stdout, stderr, true)
        } catch (e: Shell.ShellDiedException) {
            e.printStackTrace()
        }
        val stringBuilder = StringBuilder()
        for (line in stdout) {
            stringBuilder.append(line).append("\n")
        }
        return stringBuilder.toString()
    }

    private fun checkUninstallSuccessful(appApk: String): Boolean {
        val output = executeCommandSH("ls $appApk")
        return output.trim { it <= ' ' }.isEmpty()
    }

    private fun checkCommandSuccesfull(commandOutput: String?): Boolean {
        Log.e(TAG, commandOutput.toString() + " ---- ")
        return commandOutput != null && commandOutput.toLowerCase(Locale.ROOT).contains("success")
    }


    fun rebootDevice(): String {
        return executeCommandSU("reboot")
    }

    fun checkRootPermission(): RootState? {
        val hasRootedPermission: Boolean = hasRootedPermission()
        if (hasRootedPermission) return RootState.HAVE_ROOT
        val wasRooted: Boolean = wasRooted()
        return if (wasRooted) RootState.BE_ROOT else RootState.NO_ROOT
    }

    companion object {

        fun getExternalCachePath(context: Context): String =
            context.externalCacheDir!!.absolutePath.toString().substringBefore(context.packageName)

        fun getInternalCachePath(context: Context): String =
            context.cacheDir.absolutePath.toString().substringBefore(context.packageName)

        fun getSdCardCachePath(context: Context): String =
            BaseActivity.sdCardPath + "Android/data/"

    }
}