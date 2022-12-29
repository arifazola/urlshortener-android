package com.main.urlshort.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.*
import java.util.concurrent.TimeUnit

//private val URL = "https://shrlnk.my.id"
private val URL = "http://192.168.1.12:8080/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val okhttp = OkHttpClient.Builder()
    .readTimeout(60, TimeUnit.SECONDS)
    .connectTimeout(60, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(okhttp)
    .build()

interface NetworkService{
    @FormUrlEncoded
    @POST("api/signup")
    suspend fun signup(@Field("fullname") fullname: String, @Field("email") email: String, @Field("password") password: String): Respond

    @GET("api/login")
    suspend fun login(@Query("email") email: String, @Query("password") password: String) : Respond

    @GET("api/get-all-links")
    suspend fun getAllLinks(@Query("userid") userID: String, @Query("token") token: String, @Query("page") page: Int) : Respond

    @FormUrlEncoded
    @PUT("/api/links/edit")
    suspend fun editLink(@Field("url_id") urlid: String, @Field("title") title: String, @Field("back_half") backhalf: String, @Field("userid") userid: String, @Field("account_type") accountType: String, @Field("token") token: String): Respond

    @GET("api/links/getstats")
    suspend fun getStats(@Query("url_short") urlshort: String): List<StatsData>

    @FormUrlEncoded
    @POST("api/links/short")
    suspend fun shortURL(@Field("org_url") orgURL: String, @Field("input_custom") inputCustom: String, @Field("created_by") createdBy: String, @Field("account_type") accountType: String, @Field("token") token: String): Respond

    @GET("api/linkinbio/data")
    suspend fun getLib(@Query("user_id") userid: String, @Query("token") token: String) : Respond

    @GET("api/linkinbio/setting")
    suspend fun getLibSettings(@Query("property") property: String, @Query("user_id") userid: String, @Query("token") token: String): Respond

    @GET("api/linkinbio/edit")
    suspend fun editLib(@Query("user_id") userid: String, @Query("links[]") links: List<String>, @Query("titles[]") titles: List<String>, @Query("property") property: String,
                        @Query("background_type") backgroundType: String, @Query("first_color") firstColor: String, @Query("secondary_color") secondaryColor: String,
                        @Query("picture") picture: String, @Query("page_title") pageTitle: String, @Query("bio") bio: String, @Query("button_color") buttonColor: String,
                        @Query("text_color") textColor: String, @Query("token") token: String) : Respond

    @FormUrlEncoded
    @POST("api/linkinbio/create")
    suspend fun addLib(@Field("back_half") backhalf: String, @Field("created_by") createdBy: String, @Field("account_type") accountType: String, @Field("token") token: String): Respond

    @GET("api/dashboard")
    suspend fun getdatadashboard(@Query("userid") userid: String, @Query("account_type") accountType: String, @Query("token") token: String): Respond

    @FormUrlEncoded
    @POST("api/signup/authgoogle")
    suspend fun authGoogle(@Field("email") email: String, @Field("name") name: String): Respond

    @DELETE("api/links/delete/{userid}/{shortUrl}/{token}")
    suspend fun deleteLink(@Path("userid") userid: String, @Path("shortUrl") shortUrl: String, @Path("token") token: String): Respond

    @DELETE("api/lib/delete/{userid}/{shortUrl}/{token}")
    suspend fun deleteLib(@Path("userid") userid: String, @Path("shortUrl") shortUrl: String, @Path("token") token: String): Respond

    @GET("api/performance/getlinklist")
    suspend fun getLinkList(@Query("user_id") userid: String, @Query("token") token: String): Respond

    @GET("api/performance/getdata")
    suspend fun getData(@Query("links") links: String, @Query("date_start") dateStart: String, @Query("date_end") dateEnd: String, @Query("user_id") userid: String, @Query("account_type") accountType: String, @Query("token") token: String): Respond
}

object UrlShortService{
    val networkService : NetworkService by lazy {
        retrofit.create()
    }
}