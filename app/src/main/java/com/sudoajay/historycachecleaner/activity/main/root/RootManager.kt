package com.sudoajay.historycachecleaner.activity.main.root

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.sudoajay.historycachecleaner.activity.main.MainActivityViewModel
import eu.chainfire.libsuperuser.Shell
import java.io.File
import java.util.*


class RootManager(private var viewModel: MainActivityViewModel, var context: Context) {

    var TAG = "RootManager"
    private val SU_BINARY_DIRS = arrayOf(
        "/system/bin",
        "/system/sbin",
        "/system/xbin",
        "/vendor/bin",
        "/sbin"
    )


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

    fun removeCacheFolder(): Boolean {
        val commandOutput =
            executeCommandSH("rm  -rf /data/data/com.android.inputdevices/cache")
        val command = executeCommandSH("rm  -rf /data/data/com.android.inputdevices/code_cache")
        checkCommandSuccesfull(command)
        val commands =
            executeCommandSH("rm  -rf /mnt/sdcard/Android/data/com.android.bookmarkprovider/cache")
        checkCommandSuccesfull(commands)

        val output = executeCommandSH("ls /storage/")
        Log.e(TAG, output)
        return checkCommandSuccesfull(commandOutput)
    }

    private fun uninstallSystemApp(appApk: String): Boolean {
        executeCommandSU("mount -o rw,remount /system")
        executeCommandSU("rm $appApk")
        executeCommandSU("mount -o ro,remount /system")
        return checkUninstallSuccessful(appApk)
    }

    private fun uninstallSystemAppAlternativeMethod(packageName: String): Boolean {
        val commandOutput = executeCommandSU("pm uninstall --user 0 $packageName")
        return checkCommandSuccesfull(commandOutput)
    }

    private fun uninstallUserApp(packageName: String): Boolean {
        val commandOutput = executeCommandSU("pm uninstall $packageName")
        return checkCommandSuccesfull(commandOutput)
    }

    private fun uninstallUserAppUnRooted(packageName: String) {
        val packageURI = Uri.parse("package:$packageName")
        val uninstallIntent = Intent(Intent.ACTION_DELETE, packageURI)
        uninstallIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        context.startActivity(uninstallIntent)
    }

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
            Log.e(TAG, "  here - $line")
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

    companion object {

        fun getExternalCachePathFromCacheDir(context: Context?): String {
            //  Its supports till android 9
            val cachePath = (context!!.externalCacheDir?.absolutePath).toString()
            val split = cachePath.split("/Android/data/")
            return cachePath.replace(split[1],"")
        }
    }
}