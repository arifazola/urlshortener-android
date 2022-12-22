package com.main.urlshort

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.urlshort.network.Respond
import com.main.urlshort.network.UrlShortService
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val _respond = MutableLiveData<Respond?>()
    val respond: LiveData<Respond?>
    get() = _respond

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
    get() = _loading

    fun shortUrl(orgUrl: String, inputCustom: String, createdBy: String, accountType: String, token: String){
        viewModelScope.launch {
            _loading.value = true
            try {
                val short = UrlShortService.networkService.shortURL(orgUrl, inputCustom, createdBy, accountType, token)
                _respond.value = short
                _loading.value = false
            }catch (e: Exception){
                Log.e("Short URL Exception", e.message.toString())
                _loading.value = false
            }
        }
        resetValue()
    }

    private fun resetValue(){
        _respond.value = null
    }
}