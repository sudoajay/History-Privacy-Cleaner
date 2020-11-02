package com.sudoajay.historycachecleaner.activity.aboutAppActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.sudoajay.historyprivacycleaner.BuildConfig
import com.sudoajay.historyprivacycleaner.R
import com.sudoajay.historyprivacycleaner.databinding.ActivityAboutAppBinding

class AboutApp : AppCompatActivity() {
    private lateinit var binding: ActivityAboutAppBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_app)
        binding.activity = this
    }

    override fun onResume() {
        super.onResume()

        setSupportActionBar(binding.toolbar)


        binding.toolbar.setNavigationIcon(R.drawable.ic_back_solid)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun getVersionCode():String{
        val versionName = BuildConfig.VERSION_NAME

        return getString(R.string.version_info_text,versionName)
    }
}