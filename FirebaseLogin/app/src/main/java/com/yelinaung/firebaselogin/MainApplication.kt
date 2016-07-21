package com.yelinaung.firebaselogin

import android.app.Application
import com.facebook.FacebookSdk

/**
 * Created by user on 7/21/16.
 */
class MainApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(this)
    }
}
