package com.sudoajay.historycachecleaner.helper.storagePermission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.sudoajay.historycachecleaner.helper.CustomToast
import com.sudoajay.historyprivacycleaner.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class AndroidSdCardPermission(private var context: Context, private var activity: Activity?) {



    fun callPermission() {
        if (!isSdStorageWritable) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(500)
                // Its Support from Lollipop
                if (Build.VERSION.SDK_INT >= 21) {
                    CustomToast.toastIt(context, context.getString(R.string.selectSdCardMes))
                    storageAccessFrameWork()
                } else {
                    CustomToast.toastIt(context, context.getString(R.string.supportAboveSdCard))
                }

            }
        }
    }

    private fun storageAccessFrameWork() {
        try {
            val intent: Intent
            // Its Support from Lollipop
            if (Build.VERSION.SDK_INT >= 21) {
                intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                val requestCode = 42
                activity!!.startActivityForResult(intent, requestCode)
            }
        } catch (e: Exception) {
            Toast.makeText(context, "There is Error Please Report It", Toast.LENGTH_LONG).show()
        }
    }

    val isSdStorageWritable: Boolean
        get() {
            return when {
                //  Here use of DocumentFile in android 10 not File is using anymore
                Build.VERSION.SDK_INT > 28 -> getSdCardUri(context).isNotEmpty()
                        && getSdCardUri(context) != AndroidExternalStoragePermission.getExternalPathFromCacheDir(
                    context
                )
                        && !isSameUri
                Build.VERSION.SDK_INT >= 21 -> File(getSdCardPath(context)).exists()
                        && getSdCardPath(context) != AndroidExternalStoragePermission.getExternalPathFromCacheDir(
                    context
                )
                else -> {
                    true
                }
            }
        }

    private val isSameUri
        get() = AndroidExternalStoragePermission.getExternalUri(context) == getSdCardUri(context)

    companion object {

        fun getSdCardPath(context: Context) :String{
            return context.getSharedPreferences("state", Context.MODE_PRIVATE)
                .getString(
                    context.getString(R.string.sdCard_path_text), ""
                ).toString()
        }
        fun setSdCardPath(context: Context,path:String){
            context.getSharedPreferences("state", Context.MODE_PRIVATE).edit()
                .putString(
                    context.getString(R.string.sdCard_path_text),
                    path
                ).apply()
        }

        fun getSdCardUri(context: Context) :String{
            return context.getSharedPreferences("state", Context.MODE_PRIVATE)
                .getString(
                    context.getString(R.string.sdCard_uri_text), ""
                ).toString()
        }
        fun setSdCardUri(context: Context,uri:String){
            context.getSharedPreferences("state", Context.MODE_PRIVATE).edit()
                .putString(
                    context.getString(R.string.sdCard_uri_text),
                    uri
                ).apply()
        }


    }

}

