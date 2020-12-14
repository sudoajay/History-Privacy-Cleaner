package com.sudoajay.historycachecleaner.activity.settingActivity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.sudoajay.historycachecleaner.activity.aboutAppActivity.AboutApp
import com.sudoajay.historycachecleaner.activity.main.MainActivity
import com.sudoajay.historycachecleaner.activity.sendFeedback.SendFeedback
import com.sudoajay.historycachecleaner.helper.DarkModeBottomSheet
import com.sudoajay.historycachecleaner.helper.DeleteCache
import com.sudoajay.historyprivacycleaner.R


import java.util.*
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.settings,
                SettingsFragment()
            )
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private val ratingLink =
            "https://play.google.com/store/apps/details?id=com.sudoajay.duplication_data"
        private var TAG = "SettingsActivityTAG"
        private val workMangerTAG = "autoCleanIntervalTAG"

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.setting_preferences, rootKey)


            val selectInterval = findPreference("autoCleanInterval") as ListPreference?
            selectInterval!!.setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString() != getAutoCleanInterval(requireContext())) {
                    //                Here we do work Manger task :)

                    WorkManager.getInstance(requireContext()).cancelAllWorkByTag(workMangerTAG)

                    if (newValue.toString() == "never") true

                    val constraints = Constraints.Builder()
                        .setRequiresBatteryNotLow(true)
                        .setRequiresStorageNotLow(false)
                        .build()

                    val periodicWorkRequest =
                        PeriodicWorkRequestBuilder<BackGroundTask>(getHourFromInterval(newValue.toString()), TimeUnit.HOURS)
                            .setConstraints(constraints)
                            .addTag(workMangerTAG)
                            .build()


                    val workManager = WorkManager.getInstance(requireContext())

                    workManager.enqueue(periodicWorkRequest)

                    workManager.getWorkInfoByIdLiveData(periodicWorkRequest.id).observeForever {
                        if (it != null) {

                            Log.d(TAG, "Status changed to ${it.state.isFinished}")

                        }
                    }

                }

                true
            }


            val useDarkTheme =
                findPreference("useDarkTheme") as Preference?
            useDarkTheme!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                showDarkMode()
                true
            }

            val selectLanguage = findPreference("changeLanguage") as ListPreference?
            selectLanguage!!.setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString() != getLanguage(requireContext())) {
                    requireActivity().recreate()
                }
                true
            }


            val clearCache =
                findPreference("clearCache") as Preference?
            clearCache!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                DeleteCache.deleteCache(requireContext())
                true
            }

            val privacyPolicy =
                findPreference("privacyPolicy") as Preference?
            privacyPolicy!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                openPrivacyPolicy()
                true
            }

            val sendFeedback =
                findPreference("sendFeedback") as Preference?
            sendFeedback!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                sendFeedback()
                true
            }

            val reportABug =
                findPreference("reportABug") as Preference?
            reportABug!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                openGithubForReport()
                true
            }

            val shareApp =
                findPreference("shareApp") as Preference?
            shareApp!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                shareApp()
                true
            }
            val rateUs =
                findPreference("rateUs") as Preference?
            rateUs!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                rateUs()
                true
            }
            val moreApp =
                findPreference("moreApp") as Preference?
            moreApp!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                moreApp()
                true
            }
            val aboutApp =
                findPreference("aboutApp") as Preference?
            aboutApp!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //open browser or intent here
                openAboutApp()
                true
            }


        }

        private fun getHourFromInterval(value: String): Long {
            return when (value) {
                "12_hours" -> 12
                "18_hours" -> 18
                "1_day" -> 24
                "2_day" -> 48
                "3_day" -> 72
                else -> 0
            }
        }

        private fun showDarkMode() {
            val darkModeBottomSheet = DarkModeBottomSheet(MainActivity.settingShortcutId)
            darkModeBottomSheet.show(
                childFragmentManager.beginTransaction(),
                "darkModeBottomSheet"
            )

        }

        private fun openPrivacyPolicy() {
            val link = "https://play.google.com/store/apps/dev?id=5309601131127361849"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(link)
            startActivity(i)
        }


        private fun openGithubForReport() {
            val link = "https://play.google.com/store/apps/dev?id=5309601131127361849"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(link)
            startActivity(i)
        }

        private fun shareApp() {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            i.putExtra(Intent.EXTRA_SUBJECT, "Link-Share")
            i.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareMessage) + " - git " + ratingLink)
            startActivity(Intent.createChooser(i, "Share via"))
        }

         private fun rateUs() {
            val i = Intent(Intent.ACTION_VIEW)
             i.data = Uri.parse(ratingLink)
             startActivity(i)
         }

        private fun moreApp() {
            val link = "https://play.google.com/store/apps/dev?id=5309601131127361849"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(link)
            startActivity(i)
        }

        private fun openAboutApp() {
            startActivity(Intent(requireContext(), AboutApp::class.java))
        }

        private fun sendFeedback() {
            startActivity(Intent(requireContext(), SendFeedback::class.java))
        }


        companion object {
            fun getLanguage(context: Context): String {
                return PreferenceManager
                    .getDefaultSharedPreferences(context)
                    .getString("changeLanguage", setLanguage(context)).toString()
            }

            private fun setLanguage(context: Context): String {
                val lang = Locale.getDefault().language
                val array = context.resources.getStringArray(R.array.languagesValues)
                return if (lang in array) lang else "en"
            }

            fun getAutoCleanInterval(context: Context): String {
                return PreferenceManager
                    .getDefaultSharedPreferences(context)
                    .getString("autoCleanInterval", setLanguage(context)).toString()
            }
        }
    }
}