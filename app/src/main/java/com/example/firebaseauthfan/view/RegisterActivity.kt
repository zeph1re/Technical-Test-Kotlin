package com.example.firebaseauthfan.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.firebaseauthfan.R
import com.example.firebaseauthfan.model.User
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var registerButton: Button
    private lateinit var loginNavigation: TextView

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        authViewModel = AuthViewModel()

        nameInput = findViewById(R.id.register_name_input)
        emailInput = findViewById(R.id.register_email_input)
        passwordInput = findViewById(R.id.register_password_input)
        confirmPasswordInput = findViewById(R.id.register_confirm_password_input)
        registerButton = findViewById(R.id.register_button)
        loginNavigation = findViewById(R.id.login_nevigation)

        registerButton.setOnClickListener {
            proceedRegister()
        }

        goToLoginPage()

    }

    fun proceedRegister() {
        val name = nameInput.text.toString()
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()

        if (checkAllFields()) {
            lifecycleScope.launch {
                authViewModel.checkEmailAlreadyRegistered(email)
                authViewModel.registerUser(User(name,email,password, confirmPassword))
            }

            authViewModel.checkEmailRegistered.observe(this) {emailStatus ->
                when(emailStatus) {
                    "OK" -> {
                        authViewModel.registerStatus.observe(this) {
                            when(it) {
                                "OK" -> {
                                    Toast.makeText(this, "Register Success", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                }
                                "FAILED" -> {
                                    Toast.makeText(this, "Register Failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    "ALREADY REGISTERED" -> {
                        Toast.makeText(this, "Sudah terdaftar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    fun goToLoginPage() {
        loginNavigation.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkAllFields() : Boolean {
        if (nameInput.text.toString() == "") {
            nameInput.error = "This is required field"
            return false
        }

        if (nameInput.text.toString().trim().length !in 3..50) {
            nameInput.error = "Harap isi 3 - 50 karakter"
            return false
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput.text.toString()).matches()) {
            emailInput.error = "Check email format"
            return false
        }

        if (passwordInput.text.toString() == "") {
            passwordInput.error = "This is required field"
            return false
        }

        if (passwordInput.text.toString().trim().length < 8) {
            passwordInput.error = "Password must be 8 or more character"
            return false
        }

        if (passwordInput.text.toString() != confirmPasswordInput.text.toString()) {
            confirmPasswordInput.error = "Password do not match"
            return false
        }

        if (confirmPasswordInput.text.toString() == "") {
            confirmPasswordInput.error = "This is required field"
            return false
        }

        if (confirmPasswordInput.text.toString().trim().length < 8) {
            confirmPasswordInput.error = "Password must be 8 or more character"
            return false
        }

        return true
    }



}