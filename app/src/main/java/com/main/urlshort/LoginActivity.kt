package com.main.urlshort

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = this.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val userid = sharedPreferences.getString("userid", null)
        if(userid != null){
            val intent = Intent(this, MainActivity::class.java)
            this.finish()
            startActivity(intent)
        }
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
    }
}