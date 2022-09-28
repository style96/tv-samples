package com.android.tv.reference.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.tv.reference.repository.AuthAppRepository
import com.google.firebase.auth.FirebaseUser
import timber.log.Timber

class LoginRegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "LoginRegisterViewModel"
    private val authAppRepository: AuthAppRepository = AuthAppRepository()
    private val userLiveData: MutableLiveData<FirebaseUser?> = authAppRepository.getUserLiveData()
    fun login(email: String?, password: String?) {
        authAppRepository.login(email, password)
    }

    fun register(email: String?, password: String?) {
        authAppRepository.register(email, password)
    }

    fun getUserLiveData(): MutableLiveData<FirebaseUser?> {
        Timber.d("getUserLiveData : authAppRepository.getUserLiveData() " + authAppRepository.getUserLiveData())
        return userLiveData
    }
}