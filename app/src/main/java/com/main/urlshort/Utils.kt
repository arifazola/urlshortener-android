package com.main.urlshort

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.main.urlshort.signup.SignupViewModel
import java.text.SimpleDateFormat

object Utils {
    fun sharedPreferenceString(sharedPreferences: SharedPreferences, key: String, value: String){
        val editor: Editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun removeSharedPreferences(sharedPreferences: SharedPreferences, key: String){
        val editor: Editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    fun showToast(context: Context, msg: String){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun formatDate(givenDate: String, oldFormat: String, newFormat: String): String{
        val format = SimpleDateFormat(oldFormat)
        val parse = format.parse(givenDate)
        val tomil = parse.time
        val newFormat = SimpleDateFormat(newFormat).format(tomil)
        return newFormat
    }

    fun milsToDate(mils: Long, format: String): String{
        return SimpleDateFormat(format).format(mils)
    }
}