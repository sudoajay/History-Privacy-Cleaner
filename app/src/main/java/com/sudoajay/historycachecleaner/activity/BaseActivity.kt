package com.sudoajay.historycachecleaner.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.sudoajay.historycachecleaner.activity.proto.ProtoManager
import com.sudoajay.historycachecleaner.helper.LocalizationUtil.changeLocale
import com.sudoajay.historyprivacycleaner.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*


open class BaseActivity : AppCompatActivity() {
    private lateinit var currentTheme: String
    private lateinit var protoManager: ProtoManager
    override fun onCreate(savedInstanceState: Bundle?) {

        val TAG = "BaseActivityTAG"

        protoManager = ProtoManager(applicationContext)


        runBlocking {
            Log.e(TAG, "Enter in blocking thread")
            val valueDarkMode = protoManager.getStatePreferences.first().darkMode
            if (valueDarkMode.isNullOrEmpty()) {
                getDarkMode.value = getString(R.string.system_default_text)
                protoManager.setDarkMode(getString(R.string.system_default_text))
            } else
                getDarkMode.value = valueDarkMode

            isRootPermission.value = protoManager.getStatePreferences.first().isRootPermission

            Log.e(TAG, "${(protoManager.getStatePreferences.first().isSdCardFirstTimeDetected)!!}  isSdCardFirstTimeDetected and ${isSdCardFirstTimeDetected.value}")
            isSdCardFirstTimeDetected.value =
                !(protoManager.getStatePreferences.first().isSdCardFirstTimeDetected)!!
        }


        protoManager.getStatePreferences.asLiveData().observe(this) {
            if (it.darkMode != getDarkMode.value) {
                getDarkMode.value = it.darkMode
                Log.e(TAG, "${it.darkMode}  darkMode and ${getDarkMode.value}")
            }
            if (it.isDarkMode != isDarkMode.value) {
                Log.e(TAG, "${it.isDarkMode} isDarkMode and ${isDarkMode.value}")
                isDarkMode.value = it.isDarkMode
            }

            if (it.isRootPermission != isRootPermission.value) {
                Log.e(TAG, "${it.isRootPermission}  isRootPermission and ${isRootPermission.value}")
                isRootPermission.value = it.isRootPermission
            }


            if( it.isSdCardFirstTimeDetected == isSdCardFirstTimeDetected.value || isSdCardFirstTimeDetected.value == null ){
                Log.e(TAG, "${it.isSdCardFirstTimeDetected}  isSdCardFirstTimeDetected and ${isSdCardFirstTimeDetected.value}")
                isSdCardFirstTimeDetected.value = !it.isSdCardFirstTimeDetected!!
            }

            if( it.externalPath != externalPath.value){
                Log.e(TAG, "${it.externalPath}  externalPath and ${externalPath.value}")
                externalPath.value = it.externalPath
            }
            if( it.externalUri != externalUri.value){
                Log.e(TAG, "${it.externalUri}  externalUri and ${externalUri.value}")
                externalUri.value = it.externalUri
            }
            if( it.sdCardPath != sdCardPath.value){
                Log.e(TAG, "${it.sdCardPath}  sdCardPath and ${sdCardPath.value}")
                sdCardPath.value = it.sdCardPath
            }
            if( it.sdCardUri != sdCardUri.value){
                Log.e(TAG, "${it.sdCardUri}  sdCardUri and ${sdCardUri.value}")
                sdCardUri.value = it.sdCardUri
            }

            if( it.orderBy != orderBy.value){
                Log.e(TAG, "${it.orderBy}  orderBy and ${orderBy.value}")
                orderBy.value = it.orderBy
            }
            if( it.systemApps == systemApps.value || systemApps.value == null){
                Log.e(TAG, "${it.systemApps}  systemApps and ${systemApps.value}")
                systemApps.value = !it.systemApps!!
            }
            if( it.userApps == userApps.value|| userApps.value == null){
                Log.e(TAG, "${it.userApps}  userApps and ${userApps.value}")
                userApps.value = !it.userApps
            }
        }


        super.onCreate(savedInstanceState)

        currentTheme = getDarkMode.value ?: getString(R.string.system_default_text)
        setAppTheme(currentTheme)

    }


    override fun onResume() {
        super.onResume()

        val theme =
            getDarkMode.value ?: getString(R.string.system_default_text)


        if (currentTheme != theme)
            recreate()

    }

    private fun setAppTheme(currentTheme: String) {
        when (currentTheme) {
            getString(R.string.off_text) -> {
                setValue(false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            getString(
                R.string.automatic_at_sunset_text
            ) -> setDarkMode(isSunset())
            getString(
                R.string.set_by_battery_saver_text
            ) -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setValue(isPowerSaveMode())
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                    )

                } else {
                    setValue(true)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

            }
            getString(
                R.string.system_default_text
            ) -> {
                setValue(isSystemDefaultOn())
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )
            }
            else -> {
                setValue(true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

    }

    private fun setDarkMode(isDarkMode: Boolean) {
        setValue(isDarkMode)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


    private fun isSunset(): Boolean {
        val rightNow: Calendar = Calendar.getInstance()
        val hour: Int = rightNow.get(Calendar.HOUR_OF_DAY)
        return hour < 6 || hour > 18
    }


    private fun setValue(isDarkMode: Boolean) {
        BaseActivity.isDarkMode.value = isDarkMode
        lifecycleScope.launch {
            protoManager.setIsDarkMode(isDarkMode)
        }

    }


    private fun isSystemDefaultOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun isPowerSaveMode(): Boolean {
        val powerManager =
            getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isPowerSaveMode

    }


    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        overrideConfiguration?.let {
            val uiMode = it.uiMode
            it.setTo(baseContext.resources.configuration)
            it.uiMode = uiMode
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context.changeLocale("en"))
    }


    companion object {
        var getDarkMode: MutableLiveData<String> = MutableLiveData()
        var isDarkMode: MutableLiveData<Boolean> = MutableLiveData()
        var isRootPermission: MutableLiveData<Boolean> = MutableLiveData()
        var isSdCardFirstTimeDetected: MutableLiveData<Boolean> = MutableLiveData()
        var externalPath:MutableLiveData<String> = MutableLiveData()
        var externalUri : MutableLiveData<String> = MutableLiveData()
        var sdCardPath:MutableLiveData<String> = MutableLiveData()
        var sdCardUri : MutableLiveData<String> = MutableLiveData()
        val orderBy : MutableLiveData<String> = MutableLiveData()
        val systemApps : MutableLiveData<Boolean> = MutableLiveData()
        val userApps : MutableLiveData<Boolean> = MutableLiveData()

    }


}