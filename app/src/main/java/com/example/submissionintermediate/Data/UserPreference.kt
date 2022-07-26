package com.example.submissionintermediate.Data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getUser() : Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                preferences[KEY_NAME] ?: "",
                preferences[KEY_EMAIL] ?: "",
                preferences[KEY_PASS] ?: "",
                preferences[KEY_USERID] ?: "",
                preferences[KEY_TOKEN] ?: "",
                preferences[STATE] ?: false
            )
        }
    }

    suspend fun saveUser(user: User){
        dataStore.edit { preferences ->
            preferences[KEY_NAME] = user.name
            preferences[KEY_EMAIL] = user.email
            preferences[KEY_PASS] = user.password
            preferences[KEY_USERID] = user.userId
            preferences[KEY_TOKEN] = user.token
            preferences[STATE] = user.isLogin
        }
    }

    suspend fun login(){
        dataStore.edit { preferences ->
            preferences[STATE] = true
        }
    }

    suspend fun logout(){
        dataStore.edit { preferences ->
            preferences[STATE] = false
        }
    }

    companion object{
        @Volatile
        private var INSTANCE : UserPreference? = null

        private val KEY_NAME = stringPreferencesKey("name")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_PASS = stringPreferencesKey("password")
        private val KEY_USERID = stringPreferencesKey("userId")
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val STATE = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>) :UserPreference{
            return INSTANCE ?: synchronized(this){
                val instance =UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }

    }

}
