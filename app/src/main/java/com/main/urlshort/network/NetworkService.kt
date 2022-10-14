package com.main.urlshort.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private val URL = "http://192.168.1.6:8080/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface NetworkService{
    @FormUrlEncoded
    @POST("api/signup")
    suspend fun signup(@Field("fullname") fullname: String, @Field("email") email: String, @Field("password") password: String): Respond

    @GET("api/login")
    suspend fun login(@Query("email") email: String, @Query("password") password: String) : Respond

    @GET("api/get-all-links")
    suspend fun getAllLinks(@Query("userid") userID: String) : Respond
}

object UrlShortService{
    val networkService : NetworkService by lazy {
        retrofit.create()
    }
}