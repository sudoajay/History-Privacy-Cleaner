package com.sudoajay.historycachecleaner.activity.app

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.sudoajay.historycachecleaner.activity.app.database.App
import com.sudoajay.historycachecleaner.activity.app.database.AppRepository
import com.sudoajay.historycachecleaner.helper.FileHelper
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class LoadApps(
    private val context: Context,
    private val appRepository: AppRepository,
    private val allAppViewModel: AllAppViewModel
) {
    private lateinit var packageManager: PackageManager
    private var TAG = "LoadAppsTagg"
    private lateinit var fileHelper: FileHelper
    suspend fun searchInstalledApps(type: String = "Empty DataBase") {
        if (type == "Empty DataBase")
            addAppIntoDataBase(getInstalledApplication(context))
        else
            setAppSizeIntoDataBase(getInstalledApplication(context))
    }

    private fun getInstalledApplication(context: Context): List<ApplicationInfo> {
        packageManager = context.packageManager
        return packageManager.getInstalledApplications(0)
    }

    private suspend fun addAppIntoDataBase(installedApplicationsInfo: List<ApplicationInfo>) {
        for (applicationInfo in installedApplicationsInfo) {
            createApp(applicationInfo, -1)
        }
    }

    private suspend fun setAppSizeIntoDataBase(
        installedApplicationsInfo: List<ApplicationInfo>
    ) {
        fileHelper = FileHelper(context)

//        Here we first get all package list
        val packageList = appRepository.getPackageList()
//       Make a map to store package name and cache
        val cacheMap = mutableMapOf<String, Long>()
        val removePackageList = mutableListOf<String>()
        //        Here we Just add new Install App Into Data base
        for (applicationInfo in installedApplicationsInfo) {
            val packageName = getApplicationPackageName(applicationInfo)
            val cacheSize = fileHelper.fileLength(packageName)

            if (cacheSize > 8000L) cacheMap[packageName] = cacheSize
            else {
                if (packageName in packageList) removePackageList.add(packageName)
            }

        }
// update thing in data base
        allAppViewModel.stopObservingData = true
        removePackageList.forEach {
            appRepository.deleteRowFromPackage(it)
        }
        cacheMap.forEach {
            appRepository.updateCacheSizeByPackage(it.key, it.value)
        }


    }

    private suspend fun createApp(applicationInfo: ApplicationInfo, cacheSize:Long) {

        val packageName = getApplicationPackageName(applicationInfo)

            val label = getApplicationLabel(applicationInfo)
            val sourceDir = getApplicationSourceDir(applicationInfo)
            val icon = getApplicationsIcon(applicationInfo)
            val installedDate = getInstalledDate(packageName)
            val systemApp = isSystemApps(applicationInfo)
            appRepository.insert(
                App(
                    null,
                    label,
                    sourceDir,
                    packageName,
                    icon,
                    installedDate,
                    cacheSize,
                    systemApp,
                    !systemApp,
                    isSelected = true,
                    isInstalled = true
                )
            )

    }


    private fun isSystemApps(applicationInfo: ApplicationInfo): Boolean =
        applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1


    private fun getApplicationLabel(applicationInfo: ApplicationInfo): String =
        packageManager.getApplicationLabel(applicationInfo) as String


    private fun getApplicationSourceDir(applicationInfo: ApplicationInfo): String =
        applicationInfo.sourceDir


    private fun getApplicationPackageName(applicationInfo: ApplicationInfo): String =
        applicationInfo.packageName


    private fun getApplicationsIcon(applicationInfo: ApplicationInfo): String {
        return try {
            applicationInfo.processName
        } catch (e: PackageManager.NameNotFoundException) {
            "defaultApplicationIcon"
        }
    }

    private fun getInstalledDate(packageName: String): String {
        val installDate: Long? = try {
            packageManager.getPackageInfo(packageName, 0).firstInstallTime
        } catch (e: PackageManager.NameNotFoundException) {
            Calendar.getInstance().timeInMillis
        }
        return convertDateToStringFormat(Date(installDate!!))
    }

    private fun convertDateToStringFormat(date: Date): String {
        val pattern = "yyyy-MM-dd HH:mm:ss"

        val df: DateFormat = SimpleDateFormat(pattern, Locale.getDefault())

        return df.format(date)

    }



}