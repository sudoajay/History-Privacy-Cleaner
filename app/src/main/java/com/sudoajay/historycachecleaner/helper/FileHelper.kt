package com.sudoajay.historycachecleaner.helper

import android.content.Context
import android.util.Log
import com.sudoajay.historycachecleaner.helper.root.RootManager
import com.sudoajay.historycachecleaner.helper.root.RootState
import java.io.File

class FileHelper(var context: Context) {
    private var list = mutableListOf<String>()
    private val cachePath = "/cache/"
    private val codeCache = "/code_cache/"
    private val TAG = "FileListTAG"
    private val rootManager :RootManager = RootManager(context)
    private val rootState = rootManager.checkRootPermission()

    fun fileList(packageName: String): MutableList<String> {


        if (rootState == RootState.HAVE_ROOT) {
            Log.e(TAG , "PackageName - $packageName  --  is Rooted" )
            list.add(rootManager.getDirListForRoot(RootManager.getInternalCachePath(context) + packageName + cachePath))
            list.add(rootManager.getDirListForRoot(RootManager.getInternalCachePath(context) + packageName + codeCache))

            list.add(rootManager.getDirListForRoot(RootManager.getExternalCachePath(context) + packageName + cachePath))
            list.add(rootManager.getDirListForRoot(RootManager.getSdCardCachePath() + packageName + cachePath))



        }else{
            Log.e(TAG , "PackageName - $packageName  --  is not Rooted" )
//            //        Internal Path cache
//            getFilePath(File(RootManager.getInternalCachePath(context) + packageName + cachePath))
//            getFilePath(File(RootManager.getInternalCachePath(context) + packageName + codeCache))

//        External Path Cache
            getFilePath(File(RootManager.getExternalCachePath(context) + packageName + cachePath))

            //        Sd Card Path Cache
            getFilePath(File(RootManager.getSdCardCachePath() + packageName + cachePath))


        }

        return list
    }


    private fun getFilePath(dir: File) {
        Log.e(TAG, dir.absolutePath.toString())
        try {

            when {
                dir.isDirectory -> {
                    val children = dir.listFiles()
                    for (i in children!!) {
                        getFilePath(i)
                    }
                    list.add(dir.absolutePath.toString())
                }
                dir.isFile -> list.add(dir.absolutePath.toString())
            }
        } catch (e: Exception) {

        }
    }


    fun fileLength(packageName: String): Long {
        var fileLength = 0L

        if (rootState == RootState.HAVE_ROOT) {
//            Internal Cache

            fileLength += rootManager.getFileSizeForRoot(RootManager.getInternalCachePath(context) + packageName + cachePath)
            fileLength += rootManager.getFileSizeForRoot(RootManager.getInternalCachePath(context) + packageName + codeCache)

//            External and sd Card Cache
            fileLength += rootManager.getFileSizeForRoot(RootManager.getExternalCachePath(context) + packageName + cachePath)
            fileLength += rootManager.getFileSizeForRoot(RootManager.getSdCardCachePath() + packageName + cachePath)
        }else {

//            if no toot permission we cant access them
//        fileLength += getSize(File(RootManager.getInternalCachePath(context) + packageName + cachePath))
//        fileLength += getSize(File(RootManager.getInternalCachePath(context) + packageName + codeCache))


//        External Path Cache
        fileLength += dirSize(File(RootManager.getExternalCachePath(context) + packageName + cachePath))

//       Sd Card Path Cache
        fileLength += dirSize(File(RootManager.getSdCardCachePath() + packageName + cachePath))
        }
        return fileLength
    }

    /**
     * Return the size of a directory in bytes
     */
    private fun dirSize(dir: File): Long {
        if (dir.exists()) {
            var result: Long = 0
            val fileList = dir.listFiles()
            if (fileList != null) {
                for (i in fileList.indices) {
                    // Recursive call if it's a directory
                    result += if (fileList[i].isDirectory) {
                        dirSize(fileList[i])
                    } else {
                        // Sum the file size in bytes
                        fileList[i].length()
                    }
                }
            }
            return result // return the file size
        }
        return 0
    }
}