package com.sudoajay.historycachecleaner.activity.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.historyprivacycleaner.BuildConfig
import com.sudoajay.historyprivacycleaner.R
import com.sudoajay.historyprivacycleaner.databinding.LayoutNavigationDrawerBottomSheetBinding

class NavigationDrawerBottomSheet : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myDrawerView =
            layoutInflater.inflate(R.layout.layout_navigation_drawer_bottom_sheet, null)
        val binding = LayoutNavigationDrawerBottomSheetBinding.inflate(
            layoutInflater,
            myDrawerView as ViewGroup,
            false
        )
        binding.navigation = this

        return binding.root
    }


    fun rateUs() {
        val ratingLink =
            "https://play.google.com/store/apps/details?id=com.sudoajay.duplication_data"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(ratingLink)
        startActivity(i)
    }

    fun moreApp() {
        val link = "https://play.google.com/store/apps/dev?id=5309601131127361849"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(link)
        startActivity(i)
    }

    fun sendFeedback(){
//        val intent = Intent(requireContext(), SendFeedback::class.java)
//        startActivity(intent)
    }

    fun developerPage(){
        val page = "https://github.com/SudoAjay"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(page)
        startActivity(i)
    }

    fun getVersionName():String{
        val versionName: String = BuildConfig.VERSION_NAME
        return "Application version-$versionName"
    }

}

