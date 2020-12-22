package com.sudoajay.historycachecleaner.helper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.historycachecleaner.activity.proto.ProtoManager
import com.sudoajay.historyprivacycleaner.R
import com.sudoajay.historyprivacycleaner.databinding.LayoutDarkModeBottomSheetBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class DarkModeBottomSheet(private var passAction: String) : BottomSheetDialogFragment() {
    private val TAG = "DarkModeBottomSheetTAG"
    var getValue: MutableLiveData<String> = MutableLiveData()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myDrawerView = layoutInflater.inflate(R.layout.layout_dark_mode_bottom_sheet, null)
        val binding = LayoutDarkModeBottomSheetBinding.inflate(
            layoutInflater,
            myDrawerView as ViewGroup,
            false
        )
        binding.bottomSheet = this
        binding.lifecycleOwner = this


        lifecycleScope.launch {
            getValue.postValue(ProtoManager(requireContext()).getDarkMode.first().darkMode)
        }
        return binding.root
    }


    fun setValue(darkMode: String) {
        lifecycleScope.launch {
            ProtoManager(requireContext()).setDarkMode(darkMode)
            getValue.postValue(darkMode)
        }
    }
}



