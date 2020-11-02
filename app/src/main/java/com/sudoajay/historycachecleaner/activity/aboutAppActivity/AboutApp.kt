package com.sudoajay.historycachecleaner.activity.aboutAppActivity

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.sudoajay.historycachecleaner.activity.BaseActivity
import com.sudoajay.historyprivacycleaner.BuildConfig
import com.sudoajay.historyprivacycleaner.R
import com.sudoajay.historyprivacycleaner.databinding.ActivityAboutAppBinding

class AboutApp : BaseActivity() {
    private lateinit var binding: ActivityAboutAppBinding
    private var isDarkTheme: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        isDarkTheme = isDarkMode(applicationContext)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_app)
        binding.activity = this
        changeStatusBarColor()
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

    /**
     * Making notification bar transparent
     */
    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme) {
                val window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = Color.TRANSPARENT
            }
        }
    }
}