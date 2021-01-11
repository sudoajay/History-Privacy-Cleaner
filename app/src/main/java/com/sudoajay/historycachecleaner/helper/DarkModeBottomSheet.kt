package com.sudoajay.historycachecleaner.helper

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.historycachecleaner.activity.BaseActivity
import com.sudoajay.historycachecleaner.activity.main.MainActivity
import com.sudoajay.historycachecleaner.activity.proto.ProtoManager
import com.sudoajay.historyprivacycleaner.R
import com.sudoajay.historyprivacycleaner.databinding.LayoutDarkModeBottomSheetBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DarkModeBottomSheet(private var passAction: String) : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val myDrawerView = layoutInflater.inflate(R.layout.layout_dark_mode_bottom_sheet, null)
        val binding = LayoutDarkModeBottomSheetBinding.inflate(
            layoutInflater,
            myDrawerView as ViewGroup,
            false
        )

        binding.bottomSheet = this
        binding.lifecycleOwner = this
        binding.baseActivity = BaseActivity

        return binding.root
    }


    fun setValue(darkModeValue: String) {
        if (BaseActivity.getDarkMode.value == darkModeValue) dismiss()
        lifecycleScope.launch {
            ProtoManager(requireContext()).setDarkMode(darkModeValue)
            withContext(Dispatchers.Main) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.action = passAction
                requireActivity().finish()
                startActivity(intent)
            }
        }
    }

}



