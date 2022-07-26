package com.example.submissionintermediate.Data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.submissionintermediate.Login.LoginViewModel
import com.example.submissionintermediate.HomePage.MainViewModel
import com.example.submissionintermediate.Injection
import com.example.submissionintermediate.MapsPage.MapsStoryViewModel
import com.example.submissionintermediate.NewStory.NewStoryViewModel


class ViewModelFactory(private val pref: UserPreference, private val context: Context) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref, Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(NewStoryViewModel::class.java) -> {
                NewStoryViewModel(pref) as T
            }
            modelClass.isAssignableFrom(MapsStoryViewModel::class.java) -> {
                MapsStoryViewModel(pref) as T
            }
            else ->throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }


}