package com.main.urlshort

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.main.urlshort.signup.SignupViewModel

class GoogleAuth(context: Context){

    var gso: GoogleSignInOptions
    var googleSignInClient: GoogleSignInClient
    var intent: Intent

    init {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)

        intent = googleSignInClient.signInIntent
    }


    fun initialize(): Intent{
        return intent
    }

    companion object Handler{
        fun handleSignInResult(completedTask: Task<GoogleSignInAccount>, viewModel: SignupViewModel, viewLifecycleOwner: LifecycleOwner, sharedPreferences: SharedPreferences, context: Context, activity: Activity){
            try {
                val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)

                viewModel.authGoogle(account.email.toString(), account.displayName.toString())

                viewModel.isSignedupSuccess.observe(viewLifecycleOwner){
                    if(it == true){
                        val data = viewModel.respond.value
                        Utils.sharedPreferenceString(sharedPreferences, "userid", data!!.data!!.get(0).userid!!)
                        Utils.sharedPreferenceString(sharedPreferences, "fullname", data!!.data!!.get(0).fullname!!)
                        Utils.sharedPreferenceString(sharedPreferences, "email", account.email.toString())

                        val intent = Intent(context, MainActivity::class.java)
                        activity.finish()
                        activity.startActivity(intent)
                    } else if(it == false) {
                        Utils.showToast(context, "Internal server error. Please try again")
                    }
                }
            } catch (e: ApiException){
                Log.e("Login Error", e.statusCode.toString())
            }
        }
    }
}
