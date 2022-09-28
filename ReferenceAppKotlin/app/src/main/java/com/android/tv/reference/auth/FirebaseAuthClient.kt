package com.android.tv.reference.auth

import com.android.tv.reference.repository.AuthAppRepository
import com.android.tv.reference.shared.util.Result

class FirebaseAuthClient : AuthClient {
    private val authAppRepository: AuthAppRepository = AuthAppRepository()
    private val user = authAppRepository.getUserInfo()

    private suspend fun <T> wrapResult(f: suspend () -> T): Result<T> =
        try {
            Result.Success(f())
        } catch (exception: Exception) {
            Result.Error(AuthClientError.ServerError(exception))
        }
    override suspend fun validateToken(token: String): Result<UserInfo> {
        if (token == user.token) {
            return Result.Success(user)
        }
        return Result.Error(AuthClientError.AuthenticationError)
    }

    override suspend fun authWithPassword(username: String, password: String): Result<UserInfo> {
        return wrapResult {
            authAppRepository.loginSuspend(username, password)
        }
    }

    override suspend fun authWithGoogleIdToken(idToken: String): Result<UserInfo> {
        if (idToken.isEmpty()) {
            return Result.Error(AuthClientError.AuthenticationError)
        }
        return Result.Success(user)
    }

    override suspend fun invalidateToken(token: String): Result<Unit> = Result.Success(Unit)

    fun getUserInfo() : UserInfo =
        user

    companion object {
        const val MOCK_USER_EMAIL = "style93@gmail.com"
    }
}