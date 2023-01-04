package com.main.urlshort.network

import com.squareup.moshi.Json

data class Respond(
    val error: List<ErrorMsg>?,
    val data: List<DataContent>?,
    val token: String?,
)

data class DataContent(
     val userid: String?,
     val fullname: String?,
     val email: String?,
     val accountType: String?,
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
     val duplicate: String?,
     @Json(name = "most_visited_link") val mostVisitedLink: List<MostVisitedLinks>?,
     val device: List<Device>?,
     val country: List<Country>?,
     val city: List<City>?,
     val referer: List<Referer>?,
     @Json(name = "subs_growth") val subsGrowth: List<SubsGrowth>?,
     @Json(name = "total_link") val totalLink: String?,
     @Json(name = "total_lib") val totalLib: String?,
     @Json(name = "total_subs") val totalSubs: String?,
     @Json(name = "links_list") val linkList: List<LinksList>?,
     @Json(name = "create_qr") val createQr: String?
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
    val links: String?,
    val titles: String?,
    val property: String?,
    @Json(name = "background_type") val backgroundType: String?,
    @Json(name = "first_color") val firstColor: String?,
    @Json(name = "secondary_color") val secondaryColor: String?,
    @Json(name = "page_title") val pageTitle: String?,
    val bio: String?,
    @Json(name = "button_color") val buttoncolor: String?,
    @Json(name = "text_color") val text: String?,
    @Json(name = "limit_lib") val limitLib: String?,
    @Json(name = "limit_dashboard") val limitDashboard: String?,
    @Json(name = "invalid_token") val invalidToken: String?
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

data class MostVisitedLinks(
    @Json(name = "url_id") val urlid: String,
    @Json(name = "url_short") val urlshort: String
)

data class Device(
    @Json(name = "url_id") val urlid: String,
    val device: String
)

data class Country(
    @Json(name = "url_id") val urlid: String,
    val country: String
)

data class City(
    @Json(name = "url_id") val urlid: String,
    val city: String
)

data class Referer(
    @Json(name = "url_id") val urlid: String,
    val referer: String
)

data class SubsGrowth(
    val month: String,
    @Json(name = "subs_data") val subsData: String
)

data class LinksList(
    @Json(name = "url_short") val urlShort: String
)

data class CurrentLink(
    val urlid: String,
    val urlShort: String,
    val orgUrl: String,
    val qrCode: String,
    val title: String,
    val urlHit: String,
    val createdDate: String
)

data class CurrentLib(
    val urlShort: String
)
