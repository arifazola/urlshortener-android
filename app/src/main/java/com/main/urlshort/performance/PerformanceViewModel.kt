package com.main.urlshort.performance

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.urlshort.network.Respond
import com.main.urlshort.network.UrlShortService
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PerformanceViewModel: ViewModel() {

    private val _respond = MutableLiveData<Respond>()
    val respond: LiveData<Respond>
    get() = _respond

    private val _data = MutableLiveData<Respond?>()
    val data: LiveData<Respond?>
        get() = _data

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
    get() = _loading

    private var runningJob: Job? = null

    fun getLinkList(userid: String, token: String){
        val job = viewModelScope.launch {
            try {
                val linklist = UrlShortService.networkService.getLinkList(userid, token)
                _respond.value = linklist
            } catch (e: Exception){
                Log.e("Get Link List Exep", e.message.toString())
            }
        }
        runningJob = job
    }

    fun getData(link: String, dateStart: String, dateEnd: String, userid: String, accountType: String, token: String){
        viewModelScope.launch {
            _loading.value = true
            try {
                val data = UrlShortService.networkService.getData(link, dateStart, dateEnd, userid, accountType, token)
                _data.value = data
                _loading.value = false
            }catch (e: Exception){
                Log.e("Get Data Perormance Ex", e.message.toString())
                _loading.value = false
            }
        }
        resetData()
    }

    private fun resetData(){
        _data.value = null
    }

    fun cancelJob(){
        runningJob?.cancel()
    }

}