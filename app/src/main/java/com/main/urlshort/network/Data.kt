package com.main.urlshort.network

import com.squareup.moshi.Json

data class Respond(
    val error: List<ErrorMsg>?,
    val data: List<DataContent>?
)
//data class Error(
//    val error: ErrorMsg?
//)
//
//data class Data(
//    val data: DataContent?
//)

data class DataContent(
     val userid: String?,
     val fullname: String?,
     val email: String?,
     @Json(name = "url_id") val urlID: String?,
     @Json(name = "url_short") val urlShort: String?,
     @Json(name = "org_url") val orgUrl: String?,
     @Json(name = "qr_code") val qrCode: String?,
     @Json(name = "title") val title: String?,
     @Json(name = "url_hit") val urlHit: String?,
     @Json(name = "created_date") val createdDate: String?,
     val msg: Any?,
     @Json(name = "lib_id") val libId: String?,
     @Json(name = "lib_property") val libProperty: String?,
     @Json(name = "background_type") val backgroundType: String?,
     @Json(name = "first_color") val firstColor: String?,
     @Json(name = "secondary_color") val secondaryColor: String?,
     @Json(name = "picture") val picture: String?,
     @Json(name = "page_title") val pageTitle: String?,
     @Json(name = "bio") val bio: String?,
     @Json(name = "button_color") val buttoncolor: String?,
     @Json(name = "text_color") val textColor: String?,
     @Json(name = "links") val links: List<Links>?,
)

data class ErrorMsg(
    @Json(name = "error_msg") val errorMsg: String?,
    val fullname: String?,
    val email: String?,
    val password: String?,
    val title: String?,
    @Json(name = "back_half") val backHalf: String?,
    @Json(name = "org_url") val orgUrl: String?,
    @Json(name = "input_custom") val inputCustom: String?,
)

data class StatsData(
    val total: String?,
    val date: String?
)

data class Links(
    @Json(name = "lib_property") val libProperty: String?,
    val link: String?,
    val text: String?
)
