package com.main.urlshort

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.widget.Toast
import java.text.SimpleDateFormat

object Utils {
    fun sharedPreferenceString(sharedPreferences: SharedPreferences, key: String, value: String){
        val editor: Editor = sharedPreferences.edit()
        editor.putString(key, value)
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
}