package com.example.datn.utils.SharePreference

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.CoroutineContext


class UserPreferences(context: Context) {

    private val applicationContext = context.applicationContext
    private val dataStore: DataStore<Preferences> = applicationContext.createDataStore(
        name = "my_data_store"
    )

    val authToken: Flow<String?>
        get() = dataStore.data.map {
            it[KEY_AUTH]
        }

    suspend fun saveAuthToken(authToken: String) {
        dataStore.edit {
            it[KEY_AUTH] = "Bearer $authToken"
        }
    }

    companion object {
        private val KEY_AUTH = preferencesKey<String>("key_auth")
    }


}