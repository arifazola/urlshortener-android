package com.main.urlshort.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.urlshort.network.Respond
import com.main.urlshort.network.UrlShortService
import kotlinx.coroutines.launch

class DashboardViewModel: ViewModel() {

    private val _respond = MutableLiveData<Respond>()
    val respond: LiveData<Respond>
    get() = _respond

    fun getdata(userid: String, accountType: String){
        viewModelScope.launch {
            try {
                val data = UrlShortService.networkService.getdatadashboard(userid, accountType)
                _respond.value = data
            }catch (e: Exception){
                Log.e("Dashboard exception", e.message.toString())
            }
        }
    }
}