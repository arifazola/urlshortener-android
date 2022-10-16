package com.main.urlshort.linkdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.urlshort.network.Respond
import com.main.urlshort.network.UrlShortService
import kotlinx.coroutines.launch

class LinkDetailViewModel: ViewModel() {

    private val _respond = MutableLiveData<Respond?>()
    val respond: LiveData<Respond?>
    get() = _respond

    fun editLink(urlid: String, title: String, backhalf: String){
        viewModelScope.launch {
            try {
                val editLink = UrlShortService.networkService.editLink(urlid, title, backhalf)
                _respond.value = editLink
            }catch (e: Exception){
                Log.e("Edit Link Exception", e.message.toString())
            }
        }
        resetValue()
    }

    private fun resetValue(){
        _respond.value = null
    }
}