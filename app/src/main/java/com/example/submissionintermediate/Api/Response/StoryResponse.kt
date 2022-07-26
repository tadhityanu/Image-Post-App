package com.example.submissionintermediate.Api.Response

import com.example.submissionintermediate.Data.Story
import com.google.gson.annotations.SerializedName


data class StoryResponse (

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    val listStory: List<Story>
)