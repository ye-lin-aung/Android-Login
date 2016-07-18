package com.yelinaung.firebaselogin

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.mcxiaoke.koi.KoiConfig
import com.mcxiaoke.koi.log.logd
import com.yelinaung.firebaselogin.databinding.ActivityLoginBinding

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener, OnCompleteListener<AuthResult> {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var firebaselistener: FirebaseAuth.AuthStateListener? = null
    var binding: ActivityLoginBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
        KoiConfig.logEnabled = true //default is false
        // true == Log.VERBOSE
        // false == Log.ASSERT
        // optional
        KoiConfig.logLevel = Log.VERBOSE // default is Log.ASSERT
        //
        firebaselistener = this
        binding!!.emailSignInButton.setOnClickListener {
            logd { "HEre" }
            firebaseAuth.signInWithEmailAndPassword(binding!!.email.text.toString(), binding!!.password.text.toString()).addOnCompleteListener(this)
        }

    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaselistener!!);
    }

    override fun onComplete(task: Task<AuthResult>) {
        logd { "signInWithEmail:onComplete:" + task.isSuccessful() }

        // If sign in fails, display a message to the user. If sign in succeeds
        // the auth state listener will be notified and logic to handle the
        // signed in user can be handled in the listener.
        if (!task.isSuccessful()) {
            logd { "signInWithEmail" + task.getException() }
            if (task.exception is FirebaseAuthInvalidUserException) {
                firebaseAuth.createUserWithEmailAndPassword(binding!!.email.text.toString(), binding!!.password.text.toString()).addOnCompleteListener(this)
            }
            Toast.makeText(this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    override fun onStop() {
        super.onStop()
        if (firebaselistener != null) {
            firebaseAuth.removeAuthStateListener(firebaselistener!!)
        }
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        val user = firebaseAuth.currentUser
        if (user != null) {
            // User is signed in
            logd { "onAuthStateChanged:signed_in:" + user.uid }
        } else {
            // User is signed out
            logd { "onAuthStateChanged:signed_out" }
        }
    }
}
