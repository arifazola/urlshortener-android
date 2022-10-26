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
import retrofit2.http.PUT
import retrofit2.http.Query

private val URL = "http://192.168.1.7:8080/"

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

    @FormUrlEncoded
    @PUT("/api/links/edit")
    suspend fun editLink(@Field("url_id") urlid: String, @Field("title") title: String, @Field("back_half") backhalf: String): Respond

    @GET("api/links/getstats")
    suspend fun getStats(@Query("url_short") urlshort: String): List<StatsData>

    @FormUrlEncoded
    @POST("api/links/short")
    suspend fun shortURL(@Field("org_url") orgURL: String, @Field("input_custom") inputCustom: String, @Field("created_by") createdBy: String): Respond

    @GET("api/linkinbio/data")
    suspend fun getLib(@Query("user_id") userid: String) : Respond

    @GET("api/linkinbio/setting")
    suspend fun getLibSettings(@Query("property") property: String, @Query("user_id") userid: String): Respond

    @GET("api/linkinbio/edit")
    suspend fun editLib(@Query("user_id") userid: String, @Query("links[]") links: List<String>, @Query("titles[]") titles: List<String>, @Query("property") property: String,
                        @Query("background_type") backgroundType: String, @Query("first_color") firstColor: String, @Query("secondary_color") secondaryColor: String,
                        @Query("picture") picture: String, @Query("page_title") pageTitle: String, @Query("bio") bio: String, @Query("button_color") buttonColor: String,
                        @Query("text_color") textColor: String) : Respond
}

object UrlShortService{
    val networkService : NetworkService by lazy {
        retrofit.create()
    }
}