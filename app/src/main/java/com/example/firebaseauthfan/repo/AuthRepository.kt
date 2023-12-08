package com.example.firebaseauthfan.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firebaseauthfan.model.User
import com.example.firebaseauthfan.model.UserLogin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val currentUser = MutableLiveData<FirebaseUser>()
    val currentUserLiveData = currentUser

    private val checkIfEmailRegister = MutableLiveData<String>()
    val checkIfEmailRegisterLiveData : LiveData<String> = checkIfEmailRegister

    private val loginStatus = MutableLiveData<String>()
    val loginStatusLiveData: LiveData<String> = loginStatus

    private val registerStatus = MutableLiveData<String>()
    val registerStatusLiveData: LiveData<String> = registerStatus

    private val forgotPasswordStatus = MutableLiveData<String>()
    val forgotPasswordStatusLiveData : LiveData<String> = forgotPasswordStatus

    suspend fun loginUser(userLogin: UserLogin) {
        firebaseAuth.signInWithEmailAndPassword(userLogin.email, userLogin.password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    currentUser.value = firebaseAuth.currentUser
                    if (currentUser.value!!.isEmailVerified) {
                            loginStatus.postValue("OK")
                    } else {
                        loginStatus.postValue("REGISTERED")
                    }
                } else {
                    loginStatus.postValue("FAILED")
                }
            }
    }

    suspend fun registerUser(user: User) {
        firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user!!.sendEmailVerification()
                    registerStatus.postValue("OK")
                    Log.d("registerUser", "registerUser: OK")
                } else {
                    registerStatus.postValue("FAILED")
                }
            }
    }

    suspend fun checkEmailAlreadyRegistered(email :String) {
        firebaseAuth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val check = it.result.signInMethods.isNullOrEmpty()
                    if (!check) {
                        checkIfEmailRegister.postValue("OK")
                        Log.d("checkEmail", "checkEmailAlreadyRegistered: OK")
                    } else {
                        checkIfEmailRegister.postValue("ALREADY REGISTERED")
                    }
                }
            }
    }

    suspend fun forgotPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    forgotPasswordStatus.postValue("OK")
                } else {
                    forgotPasswordStatus.postValue("FAILED")
                }
            }
    }

    companion object {
        @Volatile
        private var instance : AuthRepository? = null
        fun getInstance() : AuthRepository {
            return instance ?: synchronized(this) {
                if (instance == null ){
                    instance = AuthRepository()
                }
                return instance as AuthRepository
            }
        }
    }

}