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
package com.android.tv.reference.browse

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.android.tv.reference.auth.LoginRegisterViewModel
import com.android.tv.reference.auth.SignInViewModel
import com.android.tv.reference.auth.UserManager
import com.android.tv.reference.repository.AuthAppRepository
import com.android.tv.reference.repository.VideoRepository
import com.android.tv.reference.repository.VideoRepositoryFactory
import com.android.tv.reference.shared.datamodel.VideoGroup
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class BrowseViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "BrowseViewModel"
    private val videoRepository = VideoRepositoryFactory.getVideoRepository(application)
    private val userManager = UserManager.getInstance(application.applicationContext)
    private val authAppRepository: AuthAppRepository = AuthAppRepository()
    //private val userLiveData: MutableLiveData<FirebaseUser?> = authAppRepository.getUserLiveData()
    private val isLoggedOut: MutableLiveData<Boolean>

    val browseContent = MutableLiveData<List<VideoGroup>>()
    val customMenuItems = MutableLiveData<List<BrowseCustomMenu>>(listOf())
    val isSignedIn = Transformations.map(userManager.userInfo) {
        Timber.d("userInfo livedata : " + it?.displayName)
        it != null }

    init {
        browseContent.value = getVideoGroupList(videoRepository)
        isLoggedOut = authAppRepository.getLoggedOutLiveData()
        Timber.d("init : authAppRepository.getLoggedOutLiveData() " + authAppRepository.getLoggedOutLiveData().value)

    }

    fun getVideoGroupList(repository: VideoRepository): List<VideoGroup> {
        val videosByCategory = repository.getAllVideos().groupBy { it.category }
        val videoGroupList = mutableListOf<VideoGroup>()
        videosByCategory.forEach { (k, v) ->
            videoGroupList.add(VideoGroup(k, v))
        }

        return videoGroupList
    }

    fun signOut() = viewModelScope.launch(Dispatchers.IO) {
        userManager.signOut()
        //authAppRepository.logOut()
    }
}