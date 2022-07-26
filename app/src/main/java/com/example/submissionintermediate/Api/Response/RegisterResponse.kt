package com.example.submissionintermediate.Api.Response

import com.google.gson.annotations.SerializedName


data class RegisterResponse (
    @field:SerializedName("name")
    val name : String,

    @field:SerializedName("email")
    val email : String,

    @field:SerializedName("password")
    val password : String,

    @field:SerializedName("error")
    var error : Boolean ,

    @field:SerializedName("message")
    val message : String

)