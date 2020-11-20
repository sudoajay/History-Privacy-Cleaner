package com.sudoajay.historycachecleaner.activity.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.historycachecleaner.activity.main.database.Cache
import com.sudoajay.historyprivacycleaner.R
import kotlinx.android.synthetic.main.layout_app_item.view.*


class CacheDnsAdapter( ) : RecyclerView.Adapter<CacheDnsAdapter.MyViewHolder>() {
    var items: List<Cache> = listOf()

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


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val dns = items[position]
//        holder.dnsNameTextView.text =
//            if (dns.filter == "None") dns.dnsName else dns.dnsName + " (" + dns.filter + ")"
//
//        holder.dnsBox.setOnClickListener {
//            customDns.showMoreOption(dns)
//        }


    }



}


