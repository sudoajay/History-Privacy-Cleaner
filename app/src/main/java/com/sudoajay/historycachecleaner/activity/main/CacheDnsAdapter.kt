package com.sudoajay.historycachecleaner.activity.main

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.historycachecleaner.activity.BaseActivity
import com.sudoajay.historycachecleaner.activity.app.AllApp
import com.sudoajay.historycachecleaner.activity.main.database.Cache
import com.sudoajay.historycachecleaner.helper.CustomToast
import com.sudoajay.historyprivacycleaner.R
import kotlinx.android.synthetic.main.layout_app_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class CacheDnsAdapter(var mainActivity: MainActivity) :
    RecyclerView.Adapter<CacheDnsAdapter.MyViewHolder>() {
    var items: List<Cache> = listOf()
    private var context = mainActivity.applicationContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_cache_item, parent, false)
        return MyViewHolder(layout)

    }

    class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.app_ImageView
        val title: TextView = view.appTitle_TextView
        val checkBox: CheckBox = view.app_Checkbox
        val infoContainer: ConstraintLayout = view.infoContainer_ConstraintLayout

    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val cache = items[position]


        when (cache.name) {
            context.getString(R.string.all_app_cache_text) -> {
                holder.icon.setImageResource(R.drawable.ic_more_app)
                holder.title.text = context.getString(R.string.all_app_cache_trans_text)
            }
            context.getString(R.string.download_folder_text) -> {
                holder.icon.setImageResource(R.drawable.ic_download)
                holder.title.text = context.getString(R.string.download_folder_trans_text)
            }

            context.getString(R.string.browser_default_only_text) -> {
                holder.icon.setImageResource(R.drawable.ic_browser)
                holder.title.text = context.getString(R.string.browser_default_only_trans_text)
            }
            else -> {
                holder.icon.setImageResource(R.drawable.ic_clipboard)
                holder.title.text = context.getString(R.string.clipboard_trans_text)
            }
        }

        holder.checkBox.isChecked = cache.isSelected

        holder.checkBox.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                mainActivity.viewModel.cacheRepository.updateSelectedApp(
                    (it as CompoundButton).isChecked,
                    cache.name
                )
            }
        }

        holder.infoContainer.setOnClickListener { onClickItems(cache.name) }

        holder.icon.setOnClickListener {  onClickItems(cache.name) }


    }

    private fun onClickItems(name: String){
        when (name) {
            context.getString(R.string.all_app_cache_text) -> {
                mainActivity.startActivity(Intent(context, AllApp::class.java))
            }
            context.getString(R.string.download_folder_text) ->
                openFolder()
            context.getString(R.string.clipboard_text) -> {
                generateAlertDialog()
            }
            else -> {
            }
        }
    }


    private fun openFolder() {
        val location = "/storage/emulated/0/Download/";
        val intent = Intent(Intent.ACTION_VIEW)
        val myDir: Uri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", File(location))
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            intent.setDataAndType(myDir,  DocumentsContract.Document.MIME_TYPE_DIR)
        else  intent.setDataAndType(myDir,  "*/*")

        if (intent.resolveActivityInfo(context.packageManager, 0) != null)
        {
            context.startActivity(intent)
        }
        else
        {
            // if you reach this place, it means there is no any file
            // explorer app installed on your device
            CustomToast.toastIt(context,context.getString(R.string.there_is_no_file_explorer_app_present_text))
        }
    }

    private fun getClipBoardText(): String {
        val clipBoardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData? = clipBoardManager.primaryClip
        if(   clip == null
            || clip.itemCount == 0
            || clip.itemCount > 0 && clip.getItemAt(0).text == null
        )
            return context.getString(R.string.empty_text) ; // ... whatever; just don't go to next line
        return clip.getItemAt(0).text.toString()
    }

    private fun generateAlertDialog() {
        val builder: AlertDialog.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AlertDialog.Builder(
                    mainActivity,
                    if (!BaseActivity.isDarkMode(context)) android.R.style.Theme_Material_Light_Dialog_Alert else android.R.style.Theme_Material_Dialog_Alert
                )
            } else {
                AlertDialog.Builder(context)
            }
        builder.setTitle(context.getString(R.string.clipboard_title_text))
            .setMessage(getClipBoardText())
            .setPositiveButton(context.getText(R.string.ok_text)) { _, _ ->
            }

            .setCancelable(true)
        val dialog = builder.create()
        dialog.show()
        val textView = dialog.findViewById<View>(android.R.id.message) as TextView?
        textView!!.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            context.resources.getDimension(R.dimen.alert_dialog_message_size)
        )

    }


}


