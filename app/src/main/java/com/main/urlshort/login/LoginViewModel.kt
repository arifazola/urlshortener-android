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

    fun login(email: String, password: String){
        viewModelScope.launch {
            try{
                val login = UrlShortService.networkService.login(email, password)
                _respond.value = login
                if(login.error == null){
                    _isLoggedIn.value = true
                } else {
                    _isLoggedIn.value = false
                }
            }catch (e: Exception){
                Log.e("Login Exception", e.message.toString())
            }
        }
        resetValue()
    }

    fun resetValue(){
        _isLoggedIn.value = null
    }

}