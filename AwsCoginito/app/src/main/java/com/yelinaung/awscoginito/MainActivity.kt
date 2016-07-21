package com.yelinaung.awscoginito

import android.accounts.AccountManager
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.auth.CognitoCredentialsProvider
import com.amazonaws.regions.Regions
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.mcxiaoke.koi.KoiConfig
import com.mcxiaoke.koi.async.asyncSafe
import com.mcxiaoke.koi.async.mainThreadSafe
import com.mcxiaoke.koi.ext.newIntent
import com.mcxiaoke.koi.log.logd
import com.yelinaung.awscoginito.databinding.ActivityLoginBinding
import java.util.*


class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, FacebookCallback<LoginResult>, View.OnClickListener {

    var credentialsProvider: CognitoCredentialsProvider? = null
    var acitivitybinding: ActivityLoginBinding? = null
    var mGoogleApiClient: GoogleApiClient? = null
    var callbackManager: CallbackManager? = null
    val RC_SIGN_IN = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        acitivitybinding = DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        credentialsProvider = CognitoCachingCredentialsProvider(
                baseContext, // get the context for the current activity
                BuildConfig.AWS_POOL_ID, // your identity pool id
                Regions.US_EAST_1 //Region
        )

        credentialsProvider!!.registerIdentityChangedListener { oldId, newId ->
            logd { newId }
        }
        val signInButton = findViewById(R.id.sign_in_button) as SignInButton
        signInButton.setSize(SignInButton.SIZE_WIDE)
        signInButton.setScopes(gso.scopeArray)
        signInButton.setOnClickListener(this)


        val loginButton = acitivitybinding!!.loginButton
        loginButton.setReadPermissions("email")
        // If using in a fragment

        // Other app specific specialization
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = com.facebook.CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, this)
        LoginManager.getInstance().registerCallback(callbackManager, this)


        mGoogleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build()

        KoiConfig.logEnabled = true //default is false
        KoiConfig.logLevel = Log.VERBOSE // default is Log.ASSERT
        acitivitybinding!!.emailSignInButton.setOnClickListener {
            asyncSafe {
                logd {
                    credentialsProvider!!.identityId
                }
                mainThreadSafe {
                    var extras = com.mcxiaoke.koi.ext.Bundle { putString(Intent.EXTRA_TEXT, credentialsProvider!!.identityId) }
                    startActivity(newIntent<CredentailsActivity>(extras))
                }

            }
        }


    }

    override fun onCancel() {

    }

    override fun onError(error: FacebookException?) {

    }

    override fun onSuccess(result: LoginResult?) {
        logd { result }
        asyncSafe {
            val logins = HashMap<String, String>()
            logins.put("graph.facebook.com", AccessToken.getCurrentAccessToken().token)
            logd { "Going to logged in!" }
            credentialsProvider!!.setLogins(logins)
            logd {
                credentialsProvider!!.identityId
            }
            mainThreadSafe {
                var extras = com.mcxiaoke.koi.ext.Bundle { putString(Intent.EXTRA_TEXT, credentialsProvider!!.identityId) }
                startActivity(newIntent<CredentailsActivity>(extras))
            }

        }
    }

    fun asyncLogin() {
        logd { "JKH" }
        asyncSafe {

            GooglePlayServicesUtil.isGooglePlayServicesAvailable(applicationContext)
            val am = AccountManager.get(this.getCtx())
            val accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)
            logd { "JKH2" }
            val token = GoogleAuthUtil.getToken(applicationContext, accounts[0].name,
                    "audience:server:client_id:95806688407-f3egghnci1q4kjirvpe41hp5ssu8bs9d.apps.googleusercontent.com")
            val logins = HashMap<String, String>()
            logins.put("accounts.google.com", token)
            logd { "JKH3" }
            credentialsProvider!!.setLogins(logins)
            logd {
                credentialsProvider!!.identityId
            }
            mainThreadSafe {
                var extras = com.mcxiaoke.koi.ext.Bundle { putString(Intent.EXTRA_TEXT, credentialsProvider!!.identityId) }
                startActivity(newIntent<CredentailsActivity>(extras))
            }

        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_button -> signIn()
        }// ...
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        logd { "handleSignInResult:" + result.isSuccess() }
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            val acct = result.getSignInAccount()
            logd { "handleSignInResult:" + acct.displayName }
            asyncLogin()
        } else {

        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        logd { "Connection failed" }
    }
}
