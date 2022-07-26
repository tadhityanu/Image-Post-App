package com.example.submissionintermediate.Api

import com.example.submissionintermediate.Api.Response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun uploadRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password : String
    ) :Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun uploadLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file:MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part ("lat")  lat : Double? = null,
        @Part ("lon") lon : Double? = null
    ) :Call<FileUploadResponse>

    @GET("stories")
    fun getStory(
        @Header("Authorization") token: String,
    ): Call<StoryResponse>

    @GET("stories")
    suspend fun getStoryItem(
        @Header("Authorization") token: String,
        @Query("page") page:Int,
        @Query("size") size:Int,
    ): StoryResponse

    @GET("stories")
    fun getStoryLocation(
        @Header("Authorization") token: String,
        @Query("location") location : Int = 1
    ) : Call<StoryResponse>

}