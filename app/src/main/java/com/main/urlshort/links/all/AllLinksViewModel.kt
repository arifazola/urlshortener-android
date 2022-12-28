package com.main.urlshort.links.all

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.urlshort.network.CurrentLink
import com.main.urlshort.network.Respond
import com.main.urlshort.network.UrlShortService
import kotlinx.coroutines.launch

class AllLinksViewModel: ViewModel() {

    private val _respond = MutableLiveData<Respond?>()
    val respond: LiveData<Respond?>
    get() = _respond

//    private val _links = MutableLiveData<List<CurrentLink>>()
//    val link: LiveData<List<CurrentLink>>
//    get() = _links

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
    get() = _loading

    fun getData(userid: String, token: String, page: Int){
        viewModelScope.launch {
            _loading.value = true
            try {
                val getData = UrlShortService.networkService.getAllLinks(userid, token, page)
                _respond.value = getData
                _loading.value = false
            }catch (e: Exception){
                Log.e("AllLinksException", e.message.toString())
                _loading.value = false
            }
        }
//        resetValue()
    }

    fun resetValue(){
        _respond.value = null
    }
}