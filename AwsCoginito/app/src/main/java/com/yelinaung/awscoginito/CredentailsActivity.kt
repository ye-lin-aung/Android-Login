package com.yelinaung.awscoginito

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.yelinaung.awscoginito.databinding.ActivityCredentailsBinding

class CredentailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credentails)
        val activityBinding = DataBindingUtil.setContentView<ActivityCredentailsBinding>(this, R.layout.activity_credentails);
        val id = intent.extras.getString(Intent.EXTRA_TEXT)
        activityBinding.credentials.text = id
    }
}
