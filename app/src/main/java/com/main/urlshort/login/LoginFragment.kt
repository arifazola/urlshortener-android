package com.main.urlshort.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.main.urlshort.*
import com.main.urlshort.databinding.FragmentLoginBinding
import com.main.urlshort.signup.SignupViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var sharedPreferences: SharedPreferences

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
        binding = FragmentLoginBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val tvSignup = binding.tvSignup

        tvSignup.setOnClickListener {
            findNavController().navigate(R.id.signupFragment)
        }

        binding.btnLogin.setOnClickListener {
            viewModel.isLoggedIn.removeObservers(viewLifecycleOwner)
            val email = binding.tilEmail.editText?.text.toString()
            val password = binding.tilPassword.editText?.text.toString()

            viewModel.login(email, password)

            viewModel.isLoggedIn.observe(viewLifecycleOwner){
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
                } else if(it == false){
                    val data = viewModel.respond.value
                    Utils.showToast(requireContext(), data!!.error!!.get(0).errorMsg.toString())
                }
            }
        }

        binding.cardView.setOnClickListener {
            val googleAuth = GoogleAuth(requireContext())
            val init = googleAuth.initialize()
            startActivityForResult(init, 0)
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 0){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data!!)
            val viewModel = ViewModelProvider(this).get(SignupViewModel::class.java)
            GoogleAuth.handleSignInResult(task, viewModel, viewLifecycleOwner, sharedPreferences, requireContext(), requireActivity())
        }
    }
}