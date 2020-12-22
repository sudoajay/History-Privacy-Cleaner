package com.sudoajay.historycachecleaner.activity.proto

import android.content.Context
import android.util.Log
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.createDataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.sudoajay.historycachecleaner.StatePreferences
import com.sudoajay.historyprivacycleaner.R
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

data class StatePreferences(val darkMode: String)


class ProtoManager(var context: Context) {


    private val dataStore: DataStore<StatePreferences> =
        context.createDataStore(
            fileName = "state_prefs.pb",
            serializer = StatePreferencesSerializer)

    val getDarkMode = dataStore.data
        .catch {
        if (it is IOException) {
            Log.e(TAG, "Error reading sort order preferences.", it)
            emit(StatePreferences.getDefaultInstance())
        } else {
            throw it
        }
    }.map {
            val darkMode = it.darkMode ?: context.getString(R.string.system_default_text)
            StatePreferences(darkMode)
    }

    suspend fun setDarkMode(darkMode: String?) {
        val str =darkMode ?: context.getString(R.string.system_default_text)

        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setDarkMode(str)
                .build()
        }
    }


    companion object {
        const val TAG = "ProtoMangerTAG"
    }
}



object StatePreferencesSerializer : Serializer<StatePreferences> {
    override val defaultValue: StatePreferences = StatePreferences.getDefaultInstance()
    override fun readFrom(input: InputStream): StatePreferences {
        try {
            return StatePreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override fun writeTo(t: StatePreferences, output: OutputStream) = t.writeTo(output)
}