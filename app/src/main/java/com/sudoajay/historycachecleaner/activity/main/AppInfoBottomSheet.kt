package com.sudoajay.historycachecleaner.activity.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.historycachecleaner.activity.scrolling.ScrollingAppCachePathActivity
import com.sudoajay.historycachecleaner.activity.scrolling.ScrollingAppInfoActivity
import com.sudoajay.historyprivacycleaner.R
import com.sudoajay.historyprivacycleaner.databinding.LayoutAppInfoBottomSheetBinding


class AppInfoBottomSheet(var ID:Long) : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myDrawerView =
            layoutInflater.inflate(R.layout.layout_app_info_bottom_sheet, null)
        val binding = LayoutAppInfoBottomSheetBinding.inflate(
            layoutInflater,
            myDrawerView as ViewGroup,
            false
        )
        binding.bottomSheet = this

        return binding.root
    }


     fun openAppInfo() {
         val intent = Intent(context, ScrollingAppInfoActivity::class.java)
         intent.action = ID.toString()
         intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
         requireContext().startActivity(intent)
         dismiss()
     }

    fun openAppCachePath() {
        val intent = Intent(context, ScrollingAppCachePathActivity::class.java)
        intent.action = ID.toString()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        requireContext().startActivity(intent)
        dismiss()
    }



}

