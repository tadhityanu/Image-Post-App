package com.example.submissionintermediate

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.submissionintermediate.Api.ApiConfig
import com.example.submissionintermediate.Api.Response.StoryResponse
import com.example.submissionintermediate.Data.UserPreference
import com.example.submissionintermediate.StoryPage.dataStore


object Injection {

    fun provideRepository(context: Context): StoryRepository{
        val database = StoryDatabase.getDatabase(context)
        val preference = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}