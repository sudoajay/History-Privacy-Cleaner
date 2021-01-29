package com.sudoajay.historycachecleaner.activity.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.historycachecleaner.activity.BaseActivity
import com.sudoajay.historycachecleaner.activity.proto.ProtoManager
import com.sudoajay.historyprivacycleaner.R
import com.sudoajay.historyprivacycleaner.databinding.LayoutSelectAppBottomSheetBinding
import kotlinx.coroutines.launch


class SelectAppBottomSheet : BottomSheetDialogFragment() {

    private var isSelectedAppBottomSheetFragment: FilterAppBottomSheet.IsSelectedBottomSheetFragment? = null
    private lateinit var protoManager: ProtoManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val myDrawerView =
            layoutInflater.inflate(R.layout.layout_select_app_bottom_sheet, null)
        val binding = LayoutSelectAppBottomSheetBinding.inflate(
            layoutInflater,
            myDrawerView as ViewGroup,
            false
        )
        binding.bottomSheet = this
        binding.lifecycleOwner = this
        binding.baseActivity = BaseActivity
        isSelectedAppBottomSheetFragment = activity as FilterAppBottomSheet.IsSelectedBottomSheetFragment?

        protoManager = ProtoManager(requireContext())

        return binding.root
    }

    fun setValue( value: String) {
        BaseActivity.selectOption.value = value

        lifecycleScope.launch {
                protoManager.setSelectOption(value)
            }
        isSelectedAppBottomSheetFragment!!.handleDialogClose()

        dismiss()
    }



}