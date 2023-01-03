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

    private var job: Job? = null

    fun getdata(userid: String, accountType: String, token: String){
        val runningJob = viewModelScope.launch {
            try {
                val data = UrlShortService.networkService.getdatadashboard(userid, accountType, token)
                _respond.value = data
            }catch (e: Exception){
                Log.e("Dashboard exception", e.message.toString())
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