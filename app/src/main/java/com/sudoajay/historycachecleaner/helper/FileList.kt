package com.sudoajay.historycachecleaner.helper

import android.content.Context
import android.util.Log
import com.sudoajay.historycachecleaner.activity.main.root.RootManager
import java.io.File

class FileList(var context: Context, var packageName: String) {
    private var list = mutableListOf<String>()
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
        when {
            dir.isDirectory -> {
                val children = dir.listFiles()
                for (i in children!!.indices) {
                    getFilePath(children[i])
                }
                list.add(dir.absolutePath.toString())
            }
            dir.isFile -> list.add(dir.absolutePath.toString())
        }
    }
}