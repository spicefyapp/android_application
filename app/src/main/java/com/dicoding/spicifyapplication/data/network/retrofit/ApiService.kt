package com.dicoding.spicifyapplication.data.network.retrofit

import com.dicoding.spicifyapplication.data.network.response.ChatResponse
import com.dicoding.spicifyapplication.data.network.response.LoginResponse
import com.dicoding.spicifyapplication.data.network.response.RegisterResponse
import com.dicoding.spicifyapplication.data.network.response.SpiceResponse
import com.dicoding.spicifyapplication.data.network.response.UploadResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("user/signup")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @Multipart
    @POST("predict")
    suspend fun uploadImageSpice(
        @Part file: MultipartBody.Part,
    ): UploadResponse

    @FormUrlEncoded
    @POST("chat")
    suspend fun chatWithTheBit(
        @Field("chatInput") chatInput : String
    ): ChatResponse

    @GET("rempah/show")
    suspend fun getSpices(
    ): SpiceResponse

    @GET("rempah/spiceName/{name}")
    suspend fun searchSpices(
        @Path("name") name: String
    ): SpiceResponse

}