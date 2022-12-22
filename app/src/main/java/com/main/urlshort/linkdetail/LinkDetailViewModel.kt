package com.main.urlshort.linkdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.urlshort.network.Respond
import com.main.urlshort.network.StatsData
import com.main.urlshort.network.UrlShortService
import kotlinx.coroutines.launch

class LinkDetailViewModel: ViewModel() {

    private val _respond = MutableLiveData<Respond?>()
    val respond: LiveData<Respond?>
    get() = _respond

    private val _stats = MutableLiveData<List<StatsData>>()
    val stats : LiveData<List<StatsData>>
    get() = _stats

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
    get() = _loading

    fun editLink(urlid: String, title: String, backhalf: String, userid: String, accountType: String, token: String){
        viewModelScope.launch {
            _loading.value = true
            try {
                val editLink = UrlShortService.networkService.editLink(urlid, title, backhalf, userid, accountType, token)
                _respond.value = editLink
                _loading.value = false
            }catch (e: Exception){
                Log.e("Edit Link Exception", e.message.toString())
                _loading.value = false
            }
        }
        resetValue()
    }

    fun getStats(urlshort: String){
        viewModelScope.launch {
            try {
                val getStats = UrlShortService.networkService.getStats(urlshort)
                _stats.value = getStats
            }catch (e: Exception){
                Log.e("Get Stats Exception", e.message.toString())
            }
        }
    }

    fun deleteLink(userid: String, urlshort: String, token: String){
        viewModelScope.launch {
            _loading.value = true
            try {
                val delete = UrlShortService.networkService.deleteLink(userid, urlshort, token)
                _respond.value = delete
                _loading.value = false
            }catch (e: Exception){
                Log.e("Delete Link Exception", e.message.toString())
                _loading.value = false
            }
        }
        resetValue()
    }

    private fun resetValue(){
        _respond.value = null
    }
}