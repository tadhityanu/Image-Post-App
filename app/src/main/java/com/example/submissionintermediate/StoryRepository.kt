package com.example.submissionintermediate

import android.text.style.QuoteSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.submissionintermediate.Api.ApiConfig
import com.example.submissionintermediate.Api.ApiService
import com.example.submissionintermediate.Api.Response.StoryResponse
import com.example.submissionintermediate.Data.Story
import com.example.submissionintermediate.Data.StoryPagingResource
import com.example.submissionintermediate.Data.User
import com.example.submissionintermediate.Data.UserPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository(private val database:StoryDatabase, private val apiService: ApiService) {

//    private val token = String

    fun getStory(token:String): LiveData<PagingData<Story>>{
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = {
                StoryPagingResource(apiService, token)
            }
        ).liveData
    }


}