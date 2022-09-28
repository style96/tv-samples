package com.android.tv.reference.auth

import androidx.lifecycle.LiveData
import timber.log.Timber

/**
 * This repository (from the "repository pattern") abstracts the data source from the other classes
 * that just need the data.
 *
 * The main reason for doing this is to allow the ViewModel to retrieve data without knowing
 * where it is coming from. Since it doesn't care about the details, the repository can get data
 * from a local database, an online source, or anywhere that makes sense and the ViewModel does
 * not need to change.
 */
class UserInfoRepository(private val userInfoDao: UserInfoDao) : UserInfoStorage {
    override fun readUserInfo(): LiveData<UserInfo?> {
        Timber.d("userInfoDao.getUserInfo().value.token : " + userInfoDao.getUserInfo().value?.token)
        return userInfoDao.getUserInfo()
    }

    override fun writeUserInfo(userInfo: UserInfo) {
        return userInfoDao.insert(userInfo)
    }

    override fun clearUserInfo() {
        return userInfoDao.deleteAll()
    }
}