package com.alexk.bidit

import android.app.Application
import android.content.Context
import android.util.Log
import com.alexk.bidit.common.util.sharePreference.UserTokenManager
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp


//Hilt 를 사용하기위해 반드시 선언
@HiltAndroidApp
class GlobalApplication : Application() {

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        initFirebaseSdk()
        initKakoSdk()
    }

    private fun initKakoSdk(){
        KakaoSdk.init(this,getString(R.string.KAKAO_NATIVE_KEY))
    }

    private fun initFirebaseSdk(){
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d("Get Fb Token",it)
            UserTokenManager.setPushToken(it)
        }
    }

    companion object {
        lateinit var instance: GlobalApplication
        private const val TAG: String = "LoginActivity..."
        var userId = 0
        var userNickname = ""

        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }
}