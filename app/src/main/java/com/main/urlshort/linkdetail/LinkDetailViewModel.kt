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

    fun editLink(urlid: String, title: String, backhalf: String, userid: String){
        viewModelScope.launch {
            try {
                val editLink = UrlShortService.networkService.editLink(urlid, title, backhalf, userid)
                _respond.value = editLink
            }catch (e: Exception){
                Log.e("Edit Link Exception", e.message.toString())
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

    private fun resetValue(){
        _respond.value = null
    }
}