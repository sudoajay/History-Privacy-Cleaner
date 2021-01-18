package com.sudoajay.historycachecleaner.helper.storagePermission

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.sudoajay.historycachecleaner.activity.BaseActivity
import com.sudoajay.historycachecleaner.activity.proto.ProtoManager
import com.sudoajay.historycachecleaner.helper.CustomToast
import com.sudoajay.historyprivacycleaner.R
import kotlinx.coroutines.*
import java.io.File

class AndroidSdCardPermission(private var context: Context, private var activity: Activity?) {


    fun isSdCardDetected(): Boolean {
        if (isSdStorageWritable || BaseActivity.isSdCardFirstTimeDetected.value == false) return false

        CoroutineScope(Dispatchers.IO).launch {
            ProtoManager(context).setIsSdCardFirstTimeDetected(false)
        }

        File("/storage/").listFiles()?.forEach loop@{
            if (Regex("[\\w\\d]+-[\\w\\d]+") in it.name) {
                if (Build.VERSION.SDK_INT < 21) {
                    CoroutineScope(Dispatchers.IO).launch {
                        ProtoManager(context).setSdCardPath(it.absolutePath)
                    }
                }
                CustomToast.toastIt(context, context.getString(R.string.we_detect_sd_card_text))
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    callPermissionDialog()
                }
                return true
            }
        }
        return false
    }


    private fun callPermissionDialog() {
        val alertDialog: AlertDialog.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AlertDialog.Builder(
                    activity,
                    if (BaseActivity.isDarkMode.value != false) android.R.style.Theme_Material_Light_Dialog_Alert
                    else android.R.style.Theme_Material_Dialog_Alert
                )
            } else {
                AlertDialog.Builder(activity)
            }
        alertDialog.setIcon(R.drawable.internal_storage_icon)
            .setTitle(context.getString(R.string.sd_card_storage_alert_dialog_title_text))
            .setMessage(context.getString(R.string.sd_card_storage_alert_dialog_message_text))
            .setCancelable(true)
            .setPositiveButton(R.string.yes_button_text) { _, _ ->
                callPermission()
            }
            .setNegativeButton(R.string.no_button_text) { _, _ ->
            }
            .show()
    }

    private fun callPermission() {
        if (!isSdStorageWritable) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(500)
                // Its Support from Lollipop
                if (Build.VERSION.SDK_INT >= 21) {
                    CustomToast.toastIt(context, context.getString(R.string.selectSdCardMes))
                    storageAccessFrameWork()
                } else {
                    if (!File(BaseActivity.sdCardPath.value ?: "").exists())
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
                val requestCode = 2
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
                Build.VERSION.SDK_INT > 28 -> !(BaseActivity.sdCardUri.value.isNullOrEmpty())
                        && BaseActivity.sdCardUri.value != AndroidExternalStoragePermission.getExternalPathFromCacheDir(
                    context
                )
                        && !isSameUri
                Build.VERSION.SDK_INT >= 21 -> File(BaseActivity.sdCardPath.value ?: "").exists()
                        && BaseActivity.sdCardPath.value != AndroidExternalStoragePermission.getExternalPathFromCacheDir(
                    context
                )
                else -> {
                    false
                }
            }
        }

    private val isSameUri
        get() = AndroidExternalStoragePermission.getExternalUri(context) == BaseActivity.sdCardUri.value

    companion object {

        //        fun getSdCardPath(context: Context) :String{
//            return context.getSharedPreferences("state", Context.MODE_PRIVATE)
//                .getString(
//                    context.getString(R.string.sdCard_path_text), ""
//                ).toString()
//        }
//        fun setSdCardPath(context: Context, path: String) {
//            context.getSharedPreferences("state", Context.MODE_PRIVATE).edit()
//                .putString(
//                    context.getString(R.string.sdCard_path_text),
//                    path
//                ).apply()
//        }

//        fun getSdCardUri(context: Context): String {
//            return context.getSharedPreferences("state", Context.MODE_PRIVATE)
//                .getString(
//                    context.getString(R.string.sdCard_uri_text), ""
//                ).toString()
//        }
//        fun setSdCardUri(context: Context,uri:String){
//            context.getSharedPreferences("state", Context.MODE_PRIVATE).edit()
//                .putString(
//                    context.getString(R.string.sdCard_uri_text),
//                    uri
//                ).apply()
//        }
    }

}

