package com.example.firebaseauthfan.view

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebaseauthfan.model.User
import com.example.firebaseauthfan.model.UserLogin
import com.example.firebaseauthfan.repo.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository.getInstance()

    val currentUser = authRepository.currentUserLiveData


    val loginStatus = authRepository.loginStatusLiveData
    val registerStatus = authRepository.registerStatusLiveData
    val checkEmailRegistered = authRepository.checkIfEmailRegisterLiveData
    val forgotPasswordStatus = authRepository.forgotPasswordStatusLiveData

    suspend fun registerUser(account: User) = authRepository.registerUser(account)
    suspend fun checkEmailAlreadyRegistered(email: String) = authRepository.checkEmailAlreadyRegistered(email)
    suspend fun loginUser(userLogin: UserLogin) = authRepository.loginUser(userLogin)
    suspend fun forgotPassword(email: String) = authRepository.forgotPassword(email)

}
