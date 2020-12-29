package com.sudoajay.historycachecleaner.helper.storagePermission

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.documentfile.provider.DocumentFile
import com.sudoajay.historycachecleaner.activity.BaseActivity
import com.sudoajay.historycachecleaner.helper.CustomToast
import com.sudoajay.historyprivacycleaner.R

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AndroidExternalStoragePermission(
    private var context: Context,
    private var activity: Activity
) {


    fun callPermission() { // check if permission already given or not
        if (!isExternalStorageWritable) {
            //  Here use of DocumentFile in android 10 not File is using anymore
            CoroutineScope(Dispatchers.Main).launch {
                delay(500)
                if (Build.VERSION.SDK_INT <= 28) {
                    callPermissionDialog()
                } else {
                    CustomToast.toastIt(context, context.getString(R.string.selectExternalMes))

                    storageAccessFrameWork()
                }
            }
        }
    }

    private fun callPermissionDialog() {
        val alertDialog: AlertDialog.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AlertDialog.Builder(
                    activity,
                    if (BaseActivity.isDarkMode.value != true) android.R.style.Theme_Material_Light_Dialog_Alert
                    else android.R.style.Theme_Material_Dialog_Alert
                )
            } else {
                AlertDialog.Builder(activity)
            }
        alertDialog.setIcon(R.drawable.internal_storage_icon)
            .setTitle(context.getString(R.string.external_storage_alert_dialog_title_text))
            .setMessage(context.getString(R.string.external_storage_alert_dialog_message_text))
            .setCancelable(true)
            .setPositiveButton(R.string.continueButton) { _, _ ->
                storagePermissionGranted()
            }
            .setNegativeButton(R.string.readMoreButton) { _, _ ->
                try {
                    val url = "https://developer.android.com/training/permissions/requesting.html"
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    activity.startActivity(i)
                } catch (ignored: Exception) {
                }
            }
            .show()
    }

    private fun storagePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            activity.let {
                ActivityCompat.requestPermissions(
                    it, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
        }
    }

    private fun storageAccessFrameWork() {
        try {
            val intent: Intent
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                val requestCode = 1
                activity.startActivityForResult(intent, requestCode)
            }
        } catch (e: Exception) {
            CustomToast.toastIt(context, context.getString(R.string.reportIt))

        }
    }

    val isExternalStorageWritable: Boolean
        get() {
            //
            return when {
                //  Here use of DocumentFile in android 10 not File is using anymore
                Build.VERSION.SDK_INT <= 28 -> {
                    if (Build.VERSION.SDK_INT <= 22) {
                        setExternalPath(context, getExternalPathFromCacheDir(context).toString())
                        return true
                    } else {
                        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
                        val res = activity.checkCallingOrSelfPermission(permission)
                        res == PackageManager.PERMISSION_GRANTED
                    }
                }
                else -> {
                    isSamePath ||
                            getExternalUri(context).isNotEmpty() && DocumentFile.fromTreeUri(
                        context,
                        Uri.parse(getExternalUri(context))
                    )!!.exists() && isSamePath
                }
            }

        }

    private val isSamePath: Boolean
        get() = getExternalUri(context).isNotEmpty() && getExternalPathFromCacheDir(
            context
        ).equals(
            getExternalPath(context)
        )


    companion object {
        fun getExternalPathFromCacheDir(context: Context?): String? =
            context!!.externalCacheDir!!.absolutePath.toString().substringBefore("Android/data/")

        fun getExternalPath(context: Context): String {
            return context.getSharedPreferences("state", Context.MODE_PRIVATE)
                .getString(
                    context.getString(R.string.external_path_text), ""
                ).toString()
        }

        fun setExternalPath(context: Context, path: String) {
            context.getSharedPreferences("state", Context.MODE_PRIVATE).edit()
                .putString(
                    context.getString(R.string.external_path_text),
                    path
                ).apply()
        }

        fun getExternalUri(context: Context): String {
            return context.getSharedPreferences("state", Context.MODE_PRIVATE)
                .getString(
                    context.getString(R.string.external_uri_text), ""
                ).toString()
        }

        fun setExternalUri(context: Context, uri: String) {
            context.getSharedPreferences("state", Context.MODE_PRIVATE).edit()
                .putString(
                    context.getString(R.string.external_uri_text),
                    uri
                ).apply()
        }


    }
}