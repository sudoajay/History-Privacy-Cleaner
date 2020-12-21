package com.sudoajay.historycachecleaner.helper

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.historycachecleaner.activity.main.MainActivity
import com.sudoajay.historyprivacycleaner.R
import com.sudoajay.historyprivacycleaner.databinding.LayoutDarkModeBottomSheetBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class DarkModeBottomSheet(private var passAction: String) : BottomSheetDialogFragment() {
    private val TAG = "DarkModeBottomSheetTAG"

    data class UserPreferences(val darkMode: String)

    private lateinit var dataStore: DataStore<Preferences>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myDrawerView = layoutInflater.inflate(R.layout.layout_dark_mode_bottom_sheet, null)
        val binding = LayoutDarkModeBottomSheetBinding.inflate(
            layoutInflater,
            myDrawerView as ViewGroup,
            false
        )
        binding.bottomSheet = this

        dataStore = requireContext().createDataStore(name = "State")

        return binding.root
    }

    private object PreferencesKeys {
        val DARK_MODE = preferencesKey<String>("Dark_Mode")
    }

    fun setValue(darkMode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.DARK_MODE] = darkMode
            }
            withContext(Dispatchers.Main) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.action = passAction
                requireActivity().finish()
                startActivity(intent)
            }
        }
    }

    fun getValue(): String {
        var darkMode = ""
        dataStore.data
            .catch { exception ->
                // dataStore.data throws an IOException when an error is encountered when reading data
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->
                // Get our show completed value, defaulting to false if not set:
                darkMode = preferences[PreferencesKeys.DARK_MODE]
                    ?: getString(R.string.system_default_text)
                UserPreferences(darkMode)
            }
        Log.e(TAG, darkMode)

        return darkMode
    }

//    fun setValue(value: String) {
//        if (getValue() == value) dismiss()
//        else {
//            requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
//                .putString(
//                    getString(R.string.dark_mode_text), value
//                ).apply()
//
//            val intent = Intent(requireContext(), MainActivity::class.java)
//            intent.action = passAction
//            requireActivity().finish()
//            startActivity(intent)
//
//
//        }
//    }


}

