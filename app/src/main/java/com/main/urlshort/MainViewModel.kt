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

    fun shortUrl(orgUrl: String, inputCustom: String, createdBy: String){
        viewModelScope.launch {
            try {
                val short = UrlShortService.networkService.shortURL(orgUrl, inputCustom, createdBy)
                _respond.value = short
            }catch (e: Exception){
                Log.e("Short URL Exception", e.message.toString())
            }
        }
        resetValue()
    }

    private fun resetValue(){
        _respond.value = null
    }
}