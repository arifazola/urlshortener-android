package com.main.urlshort.qr

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.urlshort.network.Respond
import com.main.urlshort.network.UrlShortService
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class QrViewModel: ViewModel() {

    private val _respond = MutableLiveData<Respond?>()
    val respond: LiveData<Respond?>
    get() = _respond

    private val _loading = MutableLiveData<Boolean?>()
    val loading: LiveData<Boolean?>
    get() = _loading

    private var runningJob: Job? = null

    fun createQR(userid: String, urlid: String, urlshort: String, token: String){
        val job = viewModelScope.launch {
            _loading.value = true
            try {
                val create = UrlShortService.networkService.createQr(userid, urlid, urlshort, token)
                _respond.value = create
                _loading.value = false
            }catch (e: Exception){
                Log.e("QR Exception", e.message.toString())
                _loading.value = false
            }
        }
        resetvalue()
        runningJob = job
    }

    fun resetvalue(){
        _respond.value = null
    }
}