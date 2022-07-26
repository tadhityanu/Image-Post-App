package com.example.submissionintermediate.NewStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.submissionintermediate.Data.User
import com.example.submissionintermediate.Data.UserPreference

class NewStoryViewModel(private val pref : UserPreference):ViewModel() {

    fun getUser(): LiveData<User> {
        return pref.getUser().asLiveData()
    }

}