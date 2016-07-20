package com.yelinaung.firebaselogin

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.yelinaung.firebaselogin.databinding.ActivityLoggedInBinding

class LoggedInActivity : AppCompatActivity() {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityBinding = DataBindingUtil.setContentView<ActivityLoggedInBinding>(this, R.layout.activity_logged_in)
        activityBinding.btnSignOut.setOnClickListener {
            firebaseAuth.signOut()
            finish()
        }

    }
}
