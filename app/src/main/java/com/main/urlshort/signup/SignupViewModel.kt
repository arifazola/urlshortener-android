package com.main.urlshort.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.urlshort.network.Respond
import com.main.urlshort.network.UrlShortService
import kotlinx.coroutines.launch
import java.io.IOException

class SignupViewModel: ViewModel() {

    private val _respond = MutableLiveData<Respond>()
    val respond: LiveData<Respond>
    get() = _respond

    private val _isSignedupSuccess = MutableLiveData<Boolean?>()
    val isSignedupSuccess: LiveData<Boolean?>
    get() = _isSignedupSuccess

    fun signup(fullname: String, email: String, password: String){
        viewModelScope.launch {
            try {
                val signup = UrlShortService.networkService.signup(fullname, email, password)
                _respond.value = signup
                if(signup.error == null){
                    _isSignedupSuccess.value = true
                } else {
                    _isSignedupSuccess.value = false
                }
            } catch (e: IOException) {
                Log.e("Signup Exception", e.message.toString())
            }
        }
        resetValue()
    }

    fun authGoogle(email: String, name: String){
        viewModelScope.launch {
            try {
                val auth = UrlShortService.networkService.authGoogle(email, name)
                _respond.value = auth
                if(auth.error == null){
                    _isSignedupSuccess.value = true
                } else {
                    _isSignedupSuccess.value = false
                }
            }catch (e: Exception){
                Log.e("Auth Google Exception", e.message.toString())
            }
        }
        resetValue()
    }

    fun resetValue(){
        _isSignedupSuccess.value = null
    }
}