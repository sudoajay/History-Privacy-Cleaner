package com.sudoajay.historycachecleaner.activity.app

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
import kotlinx.coroutines.launch


class FilterAppBottomSheet : BottomSheetDialogFragment() {

    private var isSelectedBottomSheetFragment: IsSelectedBottomSheetFragment? = null
    private lateinit var protoManager: ProtoManager
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
        protoManager = ProtoManager(requireContext())
        return binding.root
    }

    fun setOrderBy(value: String) {
        lifecycleScope.launch {
            protoManager.setOrderBy(value)

        }
        BaseActivity.orderBy.value = value
        isSelectedBottomSheetFragment!!.handleDialogClose()
        dismiss()
    }

    fun isShow(key: String): Boolean {
        return if (key == getString(R.string.menu_system_app)) BaseActivity.systemApps.value!! else BaseActivity.userApps.value!!
    }

    private fun setShow(key: String) {
        if (key == getString(R.string.menu_system_app)) {
            BaseActivity.systemApps.value = (!(BaseActivity.systemApps.value)!!)
        }
        else {
            BaseActivity.userApps.value = (!(BaseActivity.userApps.value)!!)
        }
        lifecycleScope.launch {
            if (key == getString(R.string.menu_system_app)) {
                protoManager.setSystemApps(!(BaseActivity.systemApps.value)!!)
            }
            else {
                protoManager.setUserApps(!(BaseActivity.userApps.value)!!)
            }
        }
    }
    fun setUpShow(key: String) {
        if ((key == getString(R.string.menu_system_app) && BaseActivity.systemApps.value == true && BaseActivity.userApps.value == false) or
            (key == getString(R.string.menu_user_app) && BaseActivity.systemApps.value == false && BaseActivity.userApps.value == true)
        ) {
            CustomToast.toastIt(requireContext(), getString(R.string.at_least_one_item_text))
        } else {
            setShow(key)
            isSelectedBottomSheetFragment!!.handleDialogClose()
            dismiss()
        }
    }
}

