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

data class StatePreferences(
    val darkMode: String?,
    val isDarkMode: Boolean?,
    val isRootPermission: Boolean?,
    val isSdCardFirstTimeDetected: Boolean?,
    val externalPath: String?,
    val externalUri: String?,
    val sdCardPath: String?,
    val sdCardUri: String?,
    val orderBy: String?,
    val systemApps: Boolean?,
    val userApps: Boolean,
    val selectOption: String
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
            val orderBy = it.orderBy ?: context.getString(R.string.menu_alphabetical_order)
            val selectOption = it.selectOption ?: context.getString(R.string.menu_custom_app)
            StatePreferences(
                darkMode,
                it.isDarkMode,
                it.isRootPermission,
                it.isSdCardFirstTimeDetected,
                it.externalPath,
                it.externalUri,
                it.sdCardPath,
                it.sdCardUri,
                orderBy,
                it.systemApps,
                it.userApps, selectOption
            )
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
                .setIsSdCardFirstTimeDetected(!value)
                .build()
        }
    }
    suspend fun setExternalPath(externalPath: String?){
        val value = externalPath ?: ""
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setExternalPath(value)
                .build()
        }
    }
    suspend fun setExternalUri(externalUri: String?){
        val value = externalUri ?: ""
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setExternalUri(value)
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

    suspend fun setOrderBy(orderBy: String?){
        val value = orderBy ?: context.getString(R.string.menu_alphabetical_order)
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setOrderBy(value)
                .build()
        }
    }

    suspend fun setSystemApps(systemApps: Boolean?){
        val value = systemApps ?: false
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setSystemApps(value)
                .build()
        }
    }

    suspend fun setUserApps(userApps: Boolean ?){
        val value = userApps ?: false
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setUserApps(value)
                .build()
        }
    }

    suspend fun setSelectOption(selectOption: String ?){
        val value = selectOption ?: context.getString(R.string.menu_custom_app)
        dataStore.updateData { preferences ->
            preferences.toBuilder()
                .setSelectOption(selectOption)
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