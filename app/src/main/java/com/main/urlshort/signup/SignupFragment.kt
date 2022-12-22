package com.main.urlshort.signup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.main.urlshort.*
import com.main.urlshort.databinding.FragmentSignupBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignupFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentSignupBinding
    private lateinit var viewModel: SignupViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var googleAuth: GoogleAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(SignupViewModel::class.java)
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val tvLogin = binding.tvLogin

        tvLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        viewModel.loading.observe(viewLifecycleOwner){
            if(it == true){
                showLoading()
            } else if(it == false){
                hideLoading()
            }
        }

        binding.btnSignup.setOnClickListener {
            viewModel.isSignedupSuccess.removeObservers(viewLifecycleOwner)
            val username = binding.tilUsername.editText?.text.toString()
            val email = binding.tilEmail.editText?.text.toString()
            val password = binding.tilPassword.editText?.text.toString()
            viewModel.signup(username, email, password)

            viewModel.isSignedupSuccess.observe(viewLifecycleOwner){
                if(it == true){
                    val data = viewModel.respond.value
                    Utils.sharedPreferenceString(sharedPreferences, "userid", data!!.data!!.get(0).userid!!)
                    Utils.sharedPreferenceString(sharedPreferences, "fullname", data.data!!.get(0).fullname!!)
                    Utils.sharedPreferenceString(sharedPreferences, "email", data.data.get(0).email!!)
                    Utils.sharedPreferenceString(sharedPreferences, "accountType", data.data.get(0).accountType!!)
                    Utils.sharedPreferenceString(sharedPreferences, "token", data.token.toString())

                    val intent = Intent(requireContext(), MainActivity::class.java)
                    requireActivity().finish()
                    startActivity(intent)
                } else if(it == false) {
                    val errorMsg = viewModel.respond.value
                    if(errorMsg?.error?.get(0)?.fullname != null){
                        binding.tilUsername.isErrorEnabled = true
                        binding.tilUsername.error = errorMsg.error.get(0).fullname
                    } else {
                        binding.tilUsername.isErrorEnabled = false
                    }

                    if(errorMsg?.error?.get(0)?.email != null){
                        binding.tilEmail.isErrorEnabled = true
                        binding.tilEmail.error = errorMsg.error.get(0).email
                    } else {
                        binding.tilEmail.isErrorEnabled = false
                    }

                    if(errorMsg?.error?.get(0)?.password != null){
                        binding.tilPassword.isErrorEnabled = true
                        binding.tilPassword.error = errorMsg.error.get(0).password
                    } else {
                        binding.tilPassword.isErrorEnabled = false
                    }
                }
            }
        }

        binding.cardView.setOnClickListener {
            googleAuth = GoogleAuth(requireContext())
            val init = googleAuth.initialize()
            startActivityForResult(init, 0)
        }
        return binding.root
    }

    private fun showLoading(){
        binding.tvLogo.visibility = View.GONE
        binding.tvLogin.visibility = View.GONE
        binding.tvTextBelowLogo.visibility = View.GONE
        binding.cardView.visibility = View.GONE
        binding.textView.visibility = View.GONE
        binding.tilUsername.visibility = View.GONE
        binding.tilEmail.visibility = View.GONE
        binding.tilPassword.visibility = View.GONE
        binding.btnSignup.visibility = View.GONE
        binding.loadinganim.visibility = View.VISIBLE
        binding.tvLoading.visibility = View.VISIBLE
    }

    private fun hideLoading(){
        binding.tvLogo.visibility = View.VISIBLE
        binding.tvLogin.visibility = View.VISIBLE
        binding.tvTextBelowLogo.visibility = View.VISIBLE
        binding.cardView.visibility = View.VISIBLE
        binding.textView.visibility = View.VISIBLE
        binding.tilUsername.visibility = View.VISIBLE
        binding.tilEmail.visibility = View.VISIBLE
        binding.tilPassword.visibility = View.VISIBLE
        binding.btnSignup.visibility = View.VISIBLE
        binding.loadinganim.visibility = View.GONE
        binding.tvLoading.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 0){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data!!)
            GoogleAuth.handleSignInResult(task, viewModel, viewLifecycleOwner, sharedPreferences, requireContext(), requireActivity(), googleAuth.googleSignInClient)
        }
    }
}