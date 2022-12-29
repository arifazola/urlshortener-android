package com.main.urlshort.links.all

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.urlshort.network.CurrentLink
import com.main.urlshort.network.Respond
import com.main.urlshort.network.UrlShortService
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AllLinksViewModel: ViewModel() {

    private val _respond = MutableLiveData<Respond?>()
    val respond: LiveData<Respond?>
    get() = _respond

//    private val _links = MutableLiveData<List<CurrentLink>>()
//    val link: LiveData<List<CurrentLink>>
//    get() = _links

    private val _loading = MutableLiveData<Boolean?>()
    val loading: LiveData<Boolean?>
    get() = _loading

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean>
    get() = _error

    private val _isCancelled = MutableLiveData<Boolean>()
    val isCancelled: LiveData<Boolean>
    get() = _isCancelled

    private val _errors = MutableLiveData<List<Boolean?>>()
    val errors: LiveData<List<Boolean?>>
    get() = _errors

    var runningJob: Job? = null

    fun getData(userid: String, token: String, page: Int){
        val job = viewModelScope.launch {
            _loading.value = true
            try {
                val getData = UrlShortService.networkService.getAllLinks(userid, token, page)
                _respond.value = getData
                _loading.value = false
                _error.value = false
                _isCancelled.value = false
//                _errors.value = listOf(false, false)
            }catch (e: Exception){
                Log.e("AllLinksException", e.message.toString())
                _loading.value = false
//                _error.value = true
//                _isCancelled.value = false
                if(e.message.toString() == "StandaloneCoroutine was cancelled"){
                    _error.value = false
                    _isCancelled.value = true
                } else {
                    _error.value = true
                    _isCancelled.value = false
                }
//                _errors.value = listOf(true, null)
            }
        }
        resetValue()

        runningJob = job
    }

    fun cancelJob(){
        runningJob?.cancel()
//        _errors.value = listOf(null, true)
    }

    fun resetValue(){
        _respond.value = null
    }

    fun resetLoading(){
        _loading.value = null
    }
}