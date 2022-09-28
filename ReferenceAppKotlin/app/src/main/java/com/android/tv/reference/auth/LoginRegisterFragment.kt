package com.android.tv.reference.auth

import androidx.navigation.Navigation.findNavController

import android.widget.EditText
import com.android.tv.reference.auth.LoginRegisterViewModel
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import com.android.tv.reference.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class LoginRegisterFragment : Fragment() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private var loginRegisterViewModel: LoginRegisterViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginRegisterViewModel = ViewModelProvider(this).get(
            LoginRegisterViewModel::class.java
        )
        loginRegisterViewModel!!.getUserLiveData().observe(this) { firebaseUser ->
            if (firebaseUser != null) {
                findNavController(view!!).popBackStack()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_loginregister, container, false)
        emailEditText = view.findViewById(R.id.fragment_loginregister_email)
        passwordEditText = view.findViewById(R.id.fragment_loginregister_password)
        loginButton = view.findViewById(R.id.fragment_loginregister_login)
        registerButton = view.findViewById(R.id.fragment_loginregister_register)

        loginButton.setOnClickListener(View.OnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (email.length > 0 && password.length > 0) {
                loginRegisterViewModel!!.login(email, password)
            } else {
                Toast.makeText(
                    context,
                    "Email Address and Password Must Be Entered",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        registerButton.setOnClickListener(View.OnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (email.length > 0 && password.length > 0) {
                loginRegisterViewModel!!.register(email, password)
            } else {
                Toast.makeText(
                    context,
                    "Email Address and Password Must Be Entered",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        return view
    }
}