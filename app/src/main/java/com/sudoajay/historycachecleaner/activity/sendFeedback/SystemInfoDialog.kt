package com.sudoajay.historycachecleaner.activity.sendFeedback

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.sudoajay.historycachecleaner.helper.ConnectivityType
import com.sudoajay.historycachecleaner.helper.FileSize
import com.sudoajay.historyprivacycleaner.R
import com.sudoajay.historyprivacycleaner.databinding.LayoutSystemInfoBinding


class SystemInfoDialog : DialogFragment() {
    private lateinit var binding: LayoutSystemInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_system_info,
            null,
            false
        )
        binding.dialog = this

        mainFun()

        return binding.root
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun mainFun() { // Reference Object


        // setup dialog box
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val systemInfo = SystemInfo(requireActivity())


//        Fill in the text View
        binding.deviceInfoTextTextView.text =
            systemInfo.getInfo("MANUFACTURER") + "  " + systemInfo.getInfo("MODEL") + " (" + systemInfo.getInfo(
                "PRODUCT"
            ) + ")"
        binding.osApiLevelTextTextView.text = systemInfo.getInfo("SDK_INT")
        binding.appVersionTextTextView.text = systemInfo.getAppInfo().versionName
        binding.languageTextTextView.text = systemInfo.getLanguage()
        binding.totalMemoryTextTextView.text = FileSize.convertIt(systemInfo.getHeapTotalSize())
        binding.freeMemoryTextTextView.text = FileSize.convertIt(systemInfo.getHeapFreeSize())
        binding.screenTextTextView.text =
            systemInfo.getScreenSize().heightPixels.toString() + " x " + systemInfo.getScreenSize().widthPixels.toString()
        binding.networkTypeTextTextView.text = ConnectivityType.getNetworkProvider(requireContext())

    }




    override fun onStart() { // This MUST be called first! Otherwise the view tweaking will not be present in the displayed Dialog (most likely overriden)
        super.onStart()
        forceWrapContent(this.view)
    }

    private fun forceWrapContent(v: View?) { // Start with the provided view
        var current = v
        val dm = requireContext().resources.displayMetrics
        val width = dm.widthPixels
        // Travel up the tree until fail, modifying the LayoutParams
        do { // Get the parent
            val parent = current!!.parent
            // Check if the parent exists
            if (parent != null) { // Get the view
                current = try {
                    parent as View
                } catch (e: ClassCastException) { // This will happen when at the top view, it cannot be cast to a View
                    break
                }
                // Modify the layout
                current!!.layoutParams.width = width - 10 * width / 100
            }
        } while (current!!.parent != null)
        // Request a layout to be re-done
        current!!.requestLayout()
    }


}