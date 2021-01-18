package com.sudoajay.historycachecleaner.activity.proto

import android.content.Context
import android.util.Log
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.createDataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.sudoajay.historycachecleaner.StatePreferences
import com.sudoajay.historycachecleaner.helper.storagePermission.SdCardPath
import com.sudoajay.historyprivacycleaner.R
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

data class StatePreferences(
    val darkMode: String?,
    val isDarkMode:Boolean?,
    val isRootPermission:Boolean?,
    val isSdCardFirstTimeDetected:Boolean?,
    val sdCardPath: String?,
    val sdCardUri:String?
)


class ProtoManager(var context: Context) {

    private  val TAG = "ProtoMangerTAG"

    private val dataStore: DataStore<StatePreferences> =
        context.createDataStore(
            fileName = "state_prefs.pb",
            serializer = StatePreferencesSerializer)

    val getStatePreferences = dataStore.data
        .catch {
        if (it is IOException) {
            Log.e(TAG, "Error reading sort order preferences.", it)
            emit(StatePreferences.getDefaultInstance())
        } else {
            throw it
        }
    }.map {
            val darkMode = it.darkMode ?: context.getString(R.string.system_default_text)
            StatePreferences(darkMode, it.isDarkMode,it.isRootPermission,it.isSdCardFirstTimeDetected ,it.sdCardPath,it.sdCardUri )
    }

    suspend fun setDarkMode(darkMode: String?) {
        val str =darkMode ?: context.getString(R.string.system_default_text)

        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setDarkMode(str)
                .build()
        }
    }
    suspend fun setIsDarkMode(isDarkMode: Boolean?) {
        val value =isDarkMode ?: false

        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setIsDarkMode(value)
                .build()
        }
    }
    suspend fun setIsRootPermission(isRootPermission: Boolean?) {
        val value =isRootPermission ?: false

        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setIsRootPermission(value)
                .build()
        }
    }

    suspend fun setIsSdCardFirstTimeDetected(isSdCardFirstTimeDetected: Boolean?){
        val value = isSdCardFirstTimeDetected ?: false
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setIsSdCardFirstTimeDetected(value)
                .build()
        }
    }

    suspend fun setSdCardPath(sdCardPath: String?){
        val value = sdCardPath ?: ""
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setSdCardPath(value)
                .build()
        }
    }
    suspend fun setSdCardUri(sdCardUri: String?){
        val value = sdCardUri ?: ""
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setSdCardUri(value)
                .build()
        }
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