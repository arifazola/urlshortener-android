package com.main.urlshort.performance

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.urlshort.network.Respond
import com.main.urlshort.network.UrlShortService
import kotlinx.coroutines.launch

class PerformanceViewModel: ViewModel() {

    private val _respond = MutableLiveData<Respond>()
    val respond: LiveData<Respond>
    get() = _respond

    private val _data = MutableLiveData<Respond?>()
    val data: LiveData<Respond?>
        get() = _data

    fun getLinkList(userid: String){
        viewModelScope.launch {
            try {
                val linklist = UrlShortService.networkService.getLinkList(userid)
                _respond.value = linklist
            } catch (e: Exception){
                Log.e("Get Link List Exep", e.message.toString())
            }
        }
    }

    fun getData(link: String, dateStart: String, dateEnd: String, userid: String, accountType: String){
        viewModelScope.launch {
            try {
                val data = UrlShortService.networkService.getData(link, dateStart, dateEnd, userid, accountType)
                _data.value = data
            }catch (e: Exception){
                Log.e("Get Data Perormance Ex", e.message.toString())
            }
        }
        resetData()
    }

    private fun resetData(){
        _data.value = null
    }

}