package com.sudoajay.historycachecleaner.activity.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.historycachecleaner.activity.main.database.App
import com.sudoajay.historycachecleaner.helper.FileSize
import com.sudoajay.historyprivacycleaner.R
import kotlinx.android.synthetic.main.layout_app_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PagingAppRecyclerAdapter(var context: Context, var main:MainActivity) :
    PagedListAdapter<App, PagingAppRecyclerAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private var packageManager = context.packageManager


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_app_item, parent, false)
        return MyViewHolder(layout)

    }

    class MyViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.app_ImageView
        val title: TextView = view.appTitle_TextView
        val appPackage: TextView = view.appPackage_TextView
        val size: TextView = view.sizeApp_TextView
        val checkBox: CheckBox = view.app_Checkbox
        val infoContainer: ConstraintLayout = view.infoContainer_ConstraintLayout
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val app = getItem(position)

        holder.title.text = app!!.name
        holder.appPackage.text = app.packageName
        holder.icon.setImageDrawable(getApplicationsIcon(app.icon, packageManager))

        holder.size.text = String.format("(%s)", FileSize.convertIt(app.size))
        holder.checkBox.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                main.viewModel.appRepository.updateSelectedApp(
                    (it as CompoundButton).isChecked,
                    app.packageName
                )
            }
        }
        holder.infoContainer.setOnClickListener { main.showAppInfoBottomSheet(app.id!!)}

        holder.checkBox.isChecked = app.isSelected
    }


    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<App>() {
            // Concert details may have changed if reloaded from the database,
            // but ID is fixed.
            override fun areItemsTheSame(
                oldConcert: App,
                newConcert: App
            ) = oldConcert.id == newConcert.id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldConcert: App,
                newConcert: App
            ): Boolean = oldConcert == newConcert

        }

        fun getApplicationsIcon(
            applicationInfo: String,
            packageManager: PackageManager
        ): Drawable {
            return try {
                packageManager.getApplicationIcon(applicationInfo)
            } catch (e: PackageManager.NameNotFoundException) {
                defaultApplicationIcon(packageManager)
            }
        }

        private fun defaultApplicationIcon(packageManager: PackageManager): Drawable {
            return packageManager.defaultActivityIcon
        }
    }

}