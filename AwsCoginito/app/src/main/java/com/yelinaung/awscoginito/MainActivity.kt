package com.yelinaung.awscoginito

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.yelinaung.awscoginito.databinding.ActivityLoginBinding


class MainActivity : AppCompatActivity() {
    var acitivitybinding: ActivityLoginBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        acitivitybinding = DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
//        val credentialsProvider = CognitoCachingCredentialsProvider(applicationContext, /* get the context for the application */
//                BuildConfig.AWS_POOL_ID, /* Identity Pool ID */
//                Regions.US_EAST_1)          /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
//        //credentialsProvider.withLogins()
//        credentialsProvider.registerIdentityChangedListener { oldId, newId ->
//
//            logd("Old ID $oldId And New Id $newId")
//
//        }



    }

}
