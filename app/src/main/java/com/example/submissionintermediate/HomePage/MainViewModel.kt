package com.example.submissionintermediate.HomePage

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.submissionintermediate.Api.ApiConfig
import com.example.submissionintermediate.Api.Response.StoryResponse
import com.example.submissionintermediate.Data.Story
import com.example.submissionintermediate.Data.User
import com.example.submissionintermediate.Data.UserPreference
import com.example.submissionintermediate.StoryRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref : UserPreference, private val storyRepository: StoryRepository): ViewModel() {

    fun getStoryPage (token:String) : LiveData<PagingData<Story>>{
        return storyRepository.getStory(token).cachedIn(viewModelScope)
    }

    fun getUser(): LiveData<User> {
        return pref.getUser().asLiveData()
    }

    fun logOut(){
        viewModelScope.launch {
            pref.logout()
        }
    }

    fun logIn(){
        viewModelScope.launch {
            pref.login()
        }
    }

}