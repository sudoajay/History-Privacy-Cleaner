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
import com.sudoajay.historycachecleaner.helper.CustomToast
import com.sudoajay.historyprivacycleaner.R
import com.sudoajay.historyprivacycleaner.databinding.LayoutFilterAppBottomSheetBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext


class FilterAppBottomSheet : BottomSheetDialogFragment() {

    private var isSelectedBottomSheetFragment: IsSelectedBottomSheetFragment? = null

    interface IsSelectedBottomSheetFragment {
        fun handleDialogClose()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val myDrawerView =
            layoutInflater.inflate(R.layout.layout_filter_app_bottom_sheet, null)
        val binding = LayoutFilterAppBottomSheetBinding.inflate(
            layoutInflater,
            myDrawerView as ViewGroup,
            false
        )
        binding.bottomSheet = this
        binding.lifecycleOwner = this
        binding.baseActivity = BaseActivity
        isSelectedBottomSheetFragment = activity as IsSelectedBottomSheetFragment

        return binding.root
    }

    fun setOrderBy(value: String) {
        lifecycleScope.launch {
            ProtoManager(requireContext()).setOrderBy(value)
        }
        isSelectedBottomSheetFragment!!.handleDialogClose()
        dismiss()
    }

    fun isShow(key: String): Boolean {
        return if (key == getString(R.string.menu_system_app)) BaseActivity.systemApps.value!! else BaseActivity.userApps.value!!
    }

    private fun setShow(key: String) {
        lifecycleScope.launch {
            if (key == getString(R.string.menu_system_app)) {
                ProtoManager(requireContext()).setSystemApps(!(BaseActivity.systemApps.value)!!)
                BaseActivity.systemApps.value = !(BaseActivity.systemApps.value)!!
            }
            else {
                ProtoManager(requireContext()).setUserApps(!(BaseActivity.userApps.value)!!)
                BaseActivity.userApps.value = !(BaseActivity.userApps.value)!!
            }
        }
    }
    fun setUpShow(key: String) {
        setShow(key)
        if (BaseActivity.systemApps.value == false && BaseActivity.userApps.value == false) {
            CustomToast.toastIt(requireContext(), getString(R.string.at_least_one_item_text))
            setShow(key)
        } else {
            isSelectedBottomSheetFragment!!.handleDialogClose()
            dismiss()
        }
    }
}

