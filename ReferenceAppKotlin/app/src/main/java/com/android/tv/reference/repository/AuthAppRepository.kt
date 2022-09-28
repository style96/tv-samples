package com.android.tv.reference.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.tv.reference.auth.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class AuthAppRepository {
    companion object {
        private const val TAG = "AuthAppRepository"
    }
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userLiveData: MutableLiveData<FirebaseUser?> = MutableLiveData()
    private val userInfoLiveData: MutableLiveData<UserInfo?> = MutableLiveData()
    private val loggedOutLiveData: MutableLiveData<Boolean> = MutableLiveData()

    suspend fun loginSuspend(email: String, password: String) : UserInfo {
        return try {
            val data = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val token: String = data.user!!.uid
            val userEmail: String = data.user!!.email.toString()
            userLiveData.postValue(firebaseAuth.currentUser)
            UserInfo(token, userEmail)
        } catch (e: Exception) {
            Timber.d( "loginSuspend: error " + e.message)
            throw e
        }
    }

    fun login(email: String?, password: String?) : MutableLiveData<UserInfo> {
        val authenticatedUserMutableLiveData: MutableLiveData<UserInfo> = MutableLiveData<UserInfo>()
        firebaseAuth.signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userLiveData.postValue(firebaseAuth.currentUser)
                    authenticatedUserMutableLiveData.value = getUserInfo()
                } else {
                    Timber.d( "login Failure: "+ task.exception!!.message)
                }
            }
        return authenticatedUserMutableLiveData
    }

    fun register(email: String?, password: String?) {
        firebaseAuth.createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userLiveData.postValue(firebaseAuth.currentUser)
                } else {
                    Timber.d( "register Failure: "+ task.exception!!.message)

                }
            }
    }

    fun logOut() {
        firebaseAuth.signOut()
        loggedOutLiveData.postValue(true)
    }

    fun getUserInfoLiveData(): LiveData<UserInfo?> {
        return userInfoLiveData
    }
    fun getUserLiveData(): MutableLiveData<FirebaseUser?> {
        return userLiveData
    }
    fun getUserInfo(): UserInfo {
        val displayName : String
        val token : String
        if(firebaseAuth.currentUser != null) {
            displayName = firebaseAuth.currentUser!!.email.toString()
            token = firebaseAuth.currentUser!!.uid
            return UserInfo(token, displayName)
        }
        error("user can not get")
    }

    fun getLoggedOutLiveData(): MutableLiveData<Boolean> {
        return loggedOutLiveData
    }

    init {
        if (firebaseAuth.currentUser == null) {
            userLiveData.postValue(null)
            userInfoLiveData.postValue(null)
            loggedOutLiveData.postValue(true)
        } else {
            userLiveData.postValue(firebaseAuth.currentUser)
            userInfoLiveData.postValue(getUserInfo())
            loggedOutLiveData.postValue(false)
        }
    }
}