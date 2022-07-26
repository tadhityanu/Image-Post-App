package com.example.submissionintermediate.MapsPage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.submissionintermediate.Api.ApiConfig
import com.example.submissionintermediate.Api.Response.StoryResponse
import com.example.submissionintermediate.Data.Story
import com.example.submissionintermediate.Data.User
import com.example.submissionintermediate.Data.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsStoryViewModel(private val pref : UserPreference): ViewModel() {
    val listStory = MutableLiveData<List<Story>?>()

    fun setStoriesLocation(token : String){
        Log.d(this@MapsStoryViewModel::class.java.simpleName, token)
        ApiConfig.getApiService().getStoryLocation(token = "Bearer $token")
            .enqueue(object : Callback<StoryResponse> {
                override fun onResponse(
                    call: Call<StoryResponse>,
                    response: Response<StoryResponse>
                ) {
                    if (response.isSuccessful){
                        listStory.postValue((response.body()?.listStory))
                    } else {
                        listStory.postValue(null)
                    }
                }

                override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                    listStory.postValue(null)
                }
            } )
    }

    fun getStoriesLocation():MutableLiveData<List<Story>?>{
        return listStory
    }

    fun getUser():LiveData<User>{
        return pref.getUser().asLiveData()
    }

}