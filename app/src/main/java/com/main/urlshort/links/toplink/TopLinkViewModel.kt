package com.main.urlshort.links.toplink

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.urlshort.network.Respond
import com.main.urlshort.network.UrlShortService
import kotlinx.coroutines.launch

class TopLinkViewModel: ViewModel() {

    private val _respond = MutableLiveData<Respond?>()
    val respond: LiveData<Respond?>
    get() = _respond
//
    fun getData(userid: String, token: String){
        viewModelScope.launch {
            val data = UrlShortService.networkService.getTopTen(userid, token)
            _respond.value = data
//            try {
//                val data = UrlShortService.networkService.getTopTen(userid, token)
//                _respond.value = data
//            }catch (e: Exception){
//                Log.e("Top Ten Exception", e.message.toString())
//            }
        }
    }
}