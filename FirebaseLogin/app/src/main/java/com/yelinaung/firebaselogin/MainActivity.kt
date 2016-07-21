package com.yelinaung.firebaselogin

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.mcxiaoke.koi.KoiConfig
import com.mcxiaoke.koi.ext.newIntent
import com.mcxiaoke.koi.ext.toast
import com.mcxiaoke.koi.log.logd
import com.yelinaung.firebaselogin.databinding.ActivityLoginBinding

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener, OnCompleteListener<AuthResult>, GoogleApiClient.OnConnectionFailedListener {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var firebaselistener: FirebaseAuth.AuthStateListener? = FirebaseAuth.AuthStateListener { }
    var binding: ActivityLoginBinding? = null
    var mGoogleApiClient: GoogleApiClient? = null
    var RC_SIGN_IN = 1
    var mCallbackManager: CallbackManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
        KoiConfig.logEnabled = true //default is false
        KoiConfig.logLevel = Log.VERBOSE // default is Log.ASSERT
        firebaselistener = this

        mCallbackManager = com.facebook.CallbackManager.Factory.create()
        val loginButton = findViewById(R.id.login_button) as LoginButton
        loginButton.setReadPermissions("email", "public_profile")
        loginButton.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken())
            }

            override fun onCancel() {

                // ...
            }

            override fun onError(error: FacebookException) {

                // ...
            }
        })

        if (firebaseAuth.currentUser != null) {
            startActivity(newIntent<LoggedInActivity>())
            finish()

        } else {
            toast("User isn't signed In")
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        mGoogleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build()
        binding!!.emailSignInButton.setOnClickListener {
            firebaseAuth.signInWithEmailAndPassword(binding!!.email.text.toString(), binding!!.password.text.toString()).addOnCompleteListener(this)
        }
        binding!!.emailSignUpButton.setOnClickListener {
            firebaseAuth.createUserWithEmailAndPassword(binding!!.email.text.toString(), binding!!.password.text.toString()).addOnCompleteListener(this)
        }
        binding!!.anonymousSignIn.setOnClickListener {
            firebaseAuth.signInAnonymously()
        }
        binding!!.signInButton.setOnClickListener {
            signIn()
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaselistener!!)
    }

    override fun onComplete(task: Task<AuthResult>) {
        logd { "signInWithEmail:onComplete:" + task.isSuccessful }

        if (!task.isSuccessful) {
            logd { "signInWithEmail" + task.exception }
            if (task.exception is FirebaseAuthInvalidUserException) {
                firebaseAuth.createUserWithEmailAndPassword(binding!!.email.text.toString(), binding!!.password.text.toString()).addOnCompleteListener(this)
            }
            Toast.makeText(this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show()
        } else {

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
            startActivity(newIntent<LoggedInActivity>())
            finish()
            logd { "onAuthStateChanged:signed_in:" + user.uid }
        } else {
            // User is signed out
            logd { "onAuthStateChanged:signed_out" }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                val account: GoogleSignInAccount? = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, this)

    }

    private fun handleFacebookAccessToken(token: AccessToken) {


        val credential = FacebookAuthProvider.getCredential(token.getToken())
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this,this)

                // ...


    }


    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

}

