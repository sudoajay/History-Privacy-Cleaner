package com.sudoajay.historycachecleaner.activity.aboutAppActivity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isDarkTheme )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.setDecorFitsSystemWindows(
                    false
                ) else window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_app)
        binding.activity = this
        changeStatusBarColor()
    }

    override fun onResume() {
        super.onResume()

        setSupportActionBar(binding.toolbar)


        binding.toolbar.setNavigationIcon(R.drawable.ic_back)


        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.toolbar.navigationIcon?.mutate()?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                it.setTint(
                    ContextCompat.getColor(
                        applicationContext,
                        if (isDarkTheme) R.color.backNightColor else R.color.navigationIconColor
                    )
                )
            }
            binding.toolbar.navigationIcon = it
        }
    }

    fun getVersionCode():String{
        val versionName = BuildConfig.VERSION_NAME

        return getString(R.string.version_info_text,versionName)
    }

    fun openPrivacyPolicy() {
        val link = "https://play.google.com/store/apps/dev?id=5309601131127361849"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(link)
        startActivity(i)
    }


     fun openGithub() {
        val link = "https://play.google.com/store/apps/dev?id=5309601131127361849"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(link)
        startActivity(i)
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