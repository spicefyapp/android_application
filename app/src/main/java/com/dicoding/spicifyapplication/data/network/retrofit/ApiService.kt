package com.dicoding.spicifyapplication.data.network.retrofit

import com.dicoding.spicifyapplication.data.network.response.ChatResponse
import com.dicoding.spicifyapplication.data.network.response.LoginResponse
import com.dicoding.spicifyapplication.data.network.response.RegisterResponse
import com.dicoding.spicifyapplication.data.network.response.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @FormUrlEncoded
    @POST("api/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("api/signup")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @Multipart
    @POST("predict")
    suspend fun uploadImageSpice(
//        @Header("Content-Type") type: String,
        @Part file: MultipartBody.Part,
    ): UploadResponse

    @FormUrlEncoded
    @POST("chat")
    fun chatWithTheBit(
        @Field("chatInput") chatInput : String
    ): Call<ChatResponse>


}