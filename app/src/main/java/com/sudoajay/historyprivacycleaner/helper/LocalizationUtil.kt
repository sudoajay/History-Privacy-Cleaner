package com.sudoajay.historyprivacycleaner.helper

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*


object LocalizationUtil {
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun Context.changeLocale(language:String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = this.resources.configuration
        config.setLocale(locale)
        return createConfigurationContext(config)
    }


}
