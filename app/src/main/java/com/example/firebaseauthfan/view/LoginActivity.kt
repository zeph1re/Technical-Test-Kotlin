package com.example.firebaseauthfan.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.firebaseauthfan.MainActivity
import com.example.firebaseauthfan.R
import com.example.firebaseauthfan.model.UserLogin
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {


    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordButton: TextView
    private lateinit var registerNavigation: TextView

    private lateinit var authViewModel: AuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authViewModel = AuthViewModel()

        emailInput = findViewById(R.id.login_email_input)
        passwordInput = findViewById(R.id.login_password_input)
        loginButton = findViewById(R.id.login_button)
        forgotPasswordButton = findViewById(R.id.forgot_password)
        registerNavigation = findViewById(R.id.register_navigation)

        loginButton.setOnClickListener {
            if (checkFields()) {
                proceedLogin()
            }

        }

        goToRegisterPage()

        forgotPassword()


    }

    fun proceedLogin() {

        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()

        lifecycleScope.launch {
            authViewModel.loginUser(UserLogin(email, password))
        }

        authViewModel.loginStatus.observe(this) {
            when (it) {
                "OK" -> {
                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                }

                "REGISTERED" -> {
                    Toast.makeText(this, "Email Sudah terdaftar", Toast.LENGTH_SHORT).show()
                }

                "FAILED" -> {
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun goToRegisterPage() {
        registerNavigation.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun forgotPassword() {
        val email = emailInput.text.toString()

        forgotPasswordButton.setOnClickListener {
            if (email.isNotEmpty()) {
                lifecycleScope.launch {
                    authViewModel.forgotPassword(email)
                }
            } else {
                Toast.makeText(this, "Harap isi email terlebih dahulu", Toast.LENGTH_SHORT).show()
            }

            authViewModel.forgotPasswordStatus.observe(this) {
                when (it) {
                    "OK" -> {
                        Toast.makeText(
                            this,
                            "Link Forgot Password sent to email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }

    private fun checkFields(): Boolean {
        if (emailInput.text.toString() == "") {
            emailInput.error = "This is required field"
            return false
        }

        if (passwordInput.text.toString() == "") {
            passwordInput.error = "This is required field"
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput.text.toString()).matches()) {
            emailInput.error = "Check email format"
            return false
        }

        if (passwordInput.text.toString().trim().length < 8) {
            passwordInput.error = "Password must be 8 or more character"
            return false
        }

        return true
    }


}