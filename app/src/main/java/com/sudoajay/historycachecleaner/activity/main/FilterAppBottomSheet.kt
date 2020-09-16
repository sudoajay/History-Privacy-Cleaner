package com.sudoajay.historycachecleaner.activity.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.historycachecleaner.helper.CustomToast
import com.sudoajay.historyprivacycleaner.R
import com.sudoajay.historyprivacycleaner.databinding.LayoutFilterAppBottomSheetBinding


class FilterAppBottomSheet : BottomSheetDialogFragment() {

    private var isSelectedBottomSheetFragment: IsSelectedBottomSheetFragment? = null

    interface IsSelectedBottomSheetFragment {
        fun handleDialogClose()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myDrawerView =
            layoutInflater.inflate(R.layout.layout_filter_app_bottom_sheet, null)
        val binding = LayoutFilterAppBottomSheetBinding.inflate(
            layoutInflater,
            myDrawerView as ViewGroup,
            false
        )
        binding.bottomSheet = this

        isSelectedBottomSheetFragment = activity as IsSelectedBottomSheetFragment

        return binding.root
    }


    fun isOrderBy(value: String): Boolean {
        return requireContext().getSharedPreferences("state", Context.MODE_PRIVATE)
            .getString(getString(R.string.title_menu_order_by), getString(R.string.menu_alphabetical_order)).toString() == value
    }

    fun setOrderBy(value: String) {
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putString(getString(R.string.title_menu_order_by), value).apply()
        isSelectedBottomSheetFragment!!.handleDialogClose()
        dismiss()
    }

    fun isShow(key: String): Boolean {
        return requireContext().getSharedPreferences("state", Context.MODE_PRIVATE)
            .getBoolean(key, true)
    }

    private fun setShow(key: String) {
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putBoolean(key, !isShow(key)).apply()
    }

    fun setUpShow(key: String) {
        setShow(key)

        if (!isShow(getString(R.string.menu_system_app)) && !isShow(getString(R.string.menu_user_app))) {
            CustomToast.toastIt(requireContext(), getString(R.string.at_least_one_item_text))
            setShow(key)
        } else {
            isSelectedBottomSheetFragment!!.handleDialogClose()
            dismiss()
        }
    }
}

