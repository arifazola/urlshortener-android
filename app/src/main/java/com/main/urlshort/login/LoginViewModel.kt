package com.main.urlshort.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.urlshort.network.Respond
import com.main.urlshort.network.UrlShortService
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    private val _respond = MutableLiveData<Respond>()
    val respond: LiveData<Respond>
    get() = _respond

    private val _isLoggedIn = MutableLiveData<Boolean?>()
    val isLoggedIn: LiveData<Boolean?>
    get() = _isLoggedIn

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
    get() = _loading

    fun login(email: String, password: String){
        viewModelScope.launch {
            _loading.value = true
            try{
                val login = UrlShortService.networkService.login(email, password)
                _respond.value = login
                if(login.error == null){
                    _isLoggedIn.value = true
                } else {
                    _isLoggedIn.value = false
                }
                _loading.value = false
            }catch (e: Exception){
                Log.e("Login Exception", e.message.toString())
                _loading.value = false
            }
        }
        resetValue()
    }

    fun resetValue(){
        _isLoggedIn.value = null
    }

}