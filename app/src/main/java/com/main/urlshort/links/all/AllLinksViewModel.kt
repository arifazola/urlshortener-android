package com.main.urlshort.links.all

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.urlshort.network.Respond
import com.main.urlshort.network.UrlShortService
import kotlinx.coroutines.launch

class AllLinksViewModel: ViewModel() {

    private val _respond = MutableLiveData<Respond>()
    val respond: LiveData<Respond>
    get() = _respond

    fun getData(userid: String){
        viewModelScope.launch {
            try {
                val getData = UrlShortService.networkService.getAllLinks(userid)
                _respond.value = getData
            }catch (e: Exception){
                Log.e("AllLinksException", e.message.toString())
            }
        }
    }
}