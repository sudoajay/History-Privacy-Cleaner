package com.sudoajay.historycachecleaner.helper

import android.content.Context
import android.util.Log
import com.sudoajay.historycachecleaner.helper.root.RootManager
import java.io.File
import java.lang.Exception

class FileHelper(var context: Context, var packageName: String) {
    private var list = mutableListOf<String>()
    private var fileLength = 0L
    private val cachePath = "/cache"
    private val codeCache = "/code_cache"
    private val TAG = "FileListTAG"

    fun fileList(): MutableList<String> {

//        Internal Path cache
        getFilePath(File(RootManager.getInternalCachePath(context) + packageName + cachePath))
        getFilePath(File(RootManager.getInternalCachePath(context) + packageName + codeCache))

//        External Path Cache
        getFilePath(File(RootManager.getExternalCachePath(context) + packageName + cachePath))

        //        Sd Card Path Cache
        getFilePath(File(RootManager.getSdCardCachePath(context) + packageName + cachePath))
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
        }catch (e :Exception){

        }
    }


    fun fileLength(): Long {
        //        Internal Path cache
        fileLength += File(RootManager.getInternalCachePath(context) + packageName + cachePath).length()
        fileLength += File(RootManager.getInternalCachePath(context) + packageName + codeCache).length()

//        External Path Cache
        fileLength += File(RootManager.getExternalCachePath(context) + packageName + cachePath).length()

        //        Sd Card Path Cache
        fileLength += File(RootManager.getSdCardCachePath(context) + packageName + cachePath).length()
        return fileLength
    }
}