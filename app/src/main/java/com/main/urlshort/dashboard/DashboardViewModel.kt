package com.main.urlshort.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.urlshort.network.Respond
import com.main.urlshort.network.UrlShortService
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DashboardViewModel: ViewModel() {

    private val _respond = MutableLiveData<Respond?>()
    val respond: LiveData<Respond?>
    get() = _respond

    private val _error = MutableLiveData<Boolean?>()
    val error: LiveData<Boolean?>
    get() = _error

    private var job: Job? = null

    fun getdata(userid: String, accountType: String, token: String){
        val runningJob = viewModelScope.launch {
            try {
                val data = UrlShortService.networkService.getdatadashboard(userid, accountType, token)
                _respond.value = data
                _error.value = false
            }catch (e: Exception){
                Log.e("Dashboard exception", e.message.toString())
                _error.value = true
            }
        }
        resetValue()

        job = runningJob
    }

    fun resetValue(){
        _respond.value = null
    }

    fun cancelJob(){
        job?.cancel()
    }
}