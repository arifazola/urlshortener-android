package com.main.urlshort.linkinbio

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.urlshort.network.Respond
import com.main.urlshort.network.UrlShortService
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LibViewModel: ViewModel() {

    private val _respond = MutableLiveData<Respond?>()
    val respond: LiveData<Respond?>
    get() = _respond

    private val _setting = MutableLiveData<Respond>()
    val setting: LiveData<Respond>
    get() = _setting

    private val _create = MutableLiveData<Respond?>()
    val create: LiveData<Respond?>
        get() = _create

    private val _delete = MutableLiveData<Respond?>()
    val delete: LiveData<Respond?>
        get() = _delete

    private val _loadingData = MutableLiveData<Boolean>()
    val loadingData: LiveData<Boolean>
        get() = _loadingData

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
    get() = _loading

    private val _loadingAnim = MutableLiveData<Boolean>()
    val loadingAnim: LiveData<Boolean>
        get() = _loadingAnim

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean>
        get() = _error

    private val _isCancelled = MutableLiveData<Boolean>()
    val isCancelled: LiveData<Boolean>
        get() = _isCancelled

    var runningJob: Job? = null


    fun getLibData(userid: String, token: String, page: Int){
        val job = viewModelScope.launch {
            _loadingData.value = true
            try {
                val libData = UrlShortService.networkService.getLib(userid, token, page)
                _respond.value = libData
                _loadingData.value = false
                _error.value = false
                _isCancelled.value = false
            }catch (e: Exception){
                Log.e("LibData Exception", e.message.toString())
                _loadingData.value = false
                if(e.message.toString() == "StandaloneCoroutine was cancelled"){
                    _error.value = false
                    _isCancelled.value = true
                } else {
                    _error.value = true
                    _isCancelled.value = false
                }
            }
        }

        resetValue()
        runningJob = job
    }


    fun getLibSettings(property: String, userid: String, token: String){
        viewModelScope.launch {
            try {
                val getSettings = UrlShortService.networkService.getLibSettings(property, userid, token)
                _setting.value = getSettings
            }catch (e: Exception){
                Log.e("LibSetting Exception", e.message.toString())
            }
        }
    }

    fun editlib(userid: String, links: MutableList<String>, titles: MutableList<String>, property: String, backgroundType: String, firstColor: String, secondaryColor: String,
                picture: String, pageTitle: String, bio: String, buttonColor: String, textColor: String, token: String){
        viewModelScope.launch {
            try {
                val editLib = UrlShortService.networkService.editLib(userid, links, titles, property, backgroundType, firstColor, secondaryColor, picture, pageTitle, bio, buttonColor, textColor, token)
                _respond.value = editLib
            }catch (e: Exception){
                Log.e("Edit Lib Exception", e.message.toString())
            }
        }
        resetValue()
    }

    fun createLib(backHalf: String, createdBy: String, accountType: String, token: String){
        viewModelScope.launch {
            _loading.value = true
            try {
                val addLib = UrlShortService.networkService.addLib(backHalf, createdBy, accountType, token)
                _create.value = addLib
                _loading.value = false
            }catch (e: Exception){
                Log.e("Add Lib Exception", e.message.toString())
                _loading.value = false
            }
        }
        resetCreate()
    }

    fun deleteLib(userid: String, urlShort: String, token: String){
        viewModelScope.launch {
            _loadingAnim.value = true
            try {
                val delete = UrlShortService.networkService.deleteLib(userid, urlShort, token)
                _delete.value = delete
                _loadingAnim.value = false
            }catch (e: Exception){
                Log.e("Delete Lib Exception", e.message.toString())
                _loadingAnim.value = false
            }
        }
        resetDelete()
    }

    fun cancelJob(){
        runningJob?.cancel()
//        _errors.value = listOf(null, true)
    }

    private fun resetValue(){
        _respond.value = null
    }

    private fun resetDelete(){
        _delete.value = null
    }

    private fun resetCreate(){
        _create.value = null
    }
}