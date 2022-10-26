package com.main.urlshort.linkinbio

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.urlshort.network.Respond
import com.main.urlshort.network.UrlShortService
import kotlinx.coroutines.launch

class LibViewModel: ViewModel() {

    private val _respond = MutableLiveData<Respond?>()
    val respond: LiveData<Respond?>
    get() = _respond

    private val _setting = MutableLiveData<Respond>()
    val setting: LiveData<Respond>
    get() = _setting

    fun getLibData(userid: String){
        viewModelScope.launch {
            try {
                val libData = UrlShortService.networkService.getLib(userid)
                _respond.value = libData
            }catch (e: Exception){
                Log.e("LibData Exception", e.message.toString())
            }
        }
    }

    fun getLibSettings(property: String, userid: String){
        viewModelScope.launch {
            try {
                val getSettings = UrlShortService.networkService.getLibSettings(property, userid)
                _setting.value = getSettings
            }catch (e: Exception){
                Log.e("LibSetting Exception", e.message.toString())
            }
        }
    }

    fun editlib(userid: String, links: MutableList<String>, titles: MutableList<String>, property: String, backgroundType: String, firstColor: String, secondaryColor: String,
                picture: String, pageTitle: String, bio: String, buttonColor: String, textColor: String){
        viewModelScope.launch {
            try {
                val editLib = UrlShortService.networkService.editLib(userid, links, titles, property, backgroundType, firstColor, secondaryColor, picture, pageTitle, bio, buttonColor, textColor)
                _respond.value = editLib
            }catch (e: Exception){
                Log.e("Edit Lib Exception", e.message.toString())
            }
        }
        resetValue()
    }

    private fun resetValue(){
        _respond.value = null
    }
}