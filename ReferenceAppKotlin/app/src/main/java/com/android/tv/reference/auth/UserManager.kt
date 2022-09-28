/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tv.reference.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.tv.reference.R
import com.android.tv.reference.shared.util.Result
import com.google.android.gms.auth.api.identity.SignInCredential
import timber.log.Timber

/**
 * Handles sign in and sign out process and exposes methods used to authenticate the user and
 * persist and retrieve user information.
 */
class UserManager(
    private val server: AuthClient,
    private val storage: UserInfoStorage
) {
    //private val userInfoLiveData = MutableLiveData(storage.readUserInfo().value)

    //private val userInfoLiveData = MutableLiveData(server.getUserInfo())
    val userInfo: LiveData<UserInfo?> = storage.readUserInfo()

    fun isSignedIn() = userInfo.value != null

    suspend fun signOut() {
        Timber.d("signOut : buraya geldi")
        userInfo.value?.let {
            // TODO: log server error
            server.invalidateToken(it.token)
            clearUserInfo()
        }
    }

    suspend fun authWithPassword(username: String, password: String): Result<Unit> {
        return when (val result = server.authWithPassword(username, password)) {
            is Result.Success -> {
                updateUserInfo(result.data)
                Result.Success(Unit)
            }
            is Result.Error -> result
        }
    }

    fun authWithGoogle(credential: SignInCredential): Result<Unit> {
        TODO("Not yet implemented")
    }

    suspend fun validateToken() {
        userInfo.value?.let {
            val result = server.validateToken(it.token)
            if (result is Result.Success) {
                updateUserInfo(result.data)
            } else {
                clearUserInfo()
            }
        }
    }

    private fun updateUserInfo(userInfo: UserInfo) {
        Timber.d( "updateUserInfo: UserInfo = " + userInfo.token + " " + userInfo.displayName)
        //userInfoLiveData.postValue(userInfo)
        storage.writeUserInfo(userInfo)
    }

    private fun clearUserInfo() {
        //userInfoLiveData.postValue(null)
        Timber.d("buraya geldi")
        storage.clearUserInfo()
    }

    companion object {
        const val signInFragmentId = R.id.action_global_signInFragment
        const val TAG = "UserManager"
        @Volatile
        private var INSTANCE: UserManager? = null

        fun getInstance(context: Context): UserManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: createDefault(context).also {
                    Timber.d("context : $context")
                    INSTANCE = it }
            }

        private fun createDefault(context: Context) =
            UserManager(FirebaseAuthClient(), UserInfoRepository(UserInfoDatabase.getDatabase(context).userInfoDao()))

    }
}
