package com.aawaz.tv

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import androidx.work.*
import com.aawaz.tv.analytics.Analytics
import com.aawaz.tv.analytics.AuthEvent
import com.aawaz.tv.search.SearchFragment1
import com.aawaz.tv.workers.CollectionSynchronizer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.rollbar.android.Rollbar
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit

/**
 * Created by Rinav Gangar <rinav.dev> on 30/4/20.
 * Agrahyah Technologies Pvt Ltd
 * rinav4all@gmail.com
 *
 * Entry point for the Android TV application
 */
class MainActivity : FragmentActivity() {

    private val analytics: Analytics by inject()

    private lateinit var auth: FirebaseAuth

    var searchFragment: SearchFragment1 = SearchFragment1()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Rollbar.init(this);



        // Timber.d("onCreate")
        // Navigates to other fragments based on Intent's action
        // [MainActivity] is the main entry point for all intent filters
        if (intent.action == Intent.ACTION_VIEW || intent.action == Intent.ACTION_SEARCH) {
            val uri = intent.data ?: Uri.EMPTY
            Timber.d("Intent ${intent.action} received: $uri")
        }

        // Timber.d("signIn: onCreate")
        auth = Firebase.auth

        // Syncs the home screen channels hourly
        // NOTE: It's very important to keep our content fresh in the user's home screen
        WorkManager.getInstance(baseContext).enqueue(
            PeriodicWorkRequestBuilder<CollectionSynchronizer>(12, TimeUnit.HOURS)
                .setInitialDelay(1, TimeUnit.HOURS)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        )
    }

    override fun onStart() {
        super.onStart()
        // Timber.d("signIn: onStart")

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        if (currentUser == null) {
            signInAnonymously()
        } else {
            updateUI(currentUser)
            logLoginEvent(currentUser)
        }
    }

    public override fun onStop() {
        super.onStop()
        // Timber.d("signIn: onStop")
        hideProgressBar()
    }

    private fun signInAnonymously() {
        //  Timber.d("signIn: signInAnonymously")
        showProgressBar()

        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->

                if (task.isComplete) {
                    //Timber.d("signIn:task complete")

                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Timber.d("signIn:success")
                        val user = auth.currentUser
                        updateUI(user)
                        logSignUpEvent(user, "success", "")
                    } else {
                        Rollbar.instance().log(task.exception)
                        // If sign in fails, display a message to the user.
                        Timber.w(task.exception, "signIn:failure")
//                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                        logSignUpEvent(null, "failure", task.exception?.message)
                    }
                } else {
                    Timber.d("signIn:task not complete")
                }
                hideProgressBar()
            }
    }

//    private fun signOut() {
//        auth.signOut()
//        updateUI(null)
//    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressBar()
        val isSignedIn = user != null

        if (isSignedIn) {
            user?.let {
                Timber.d("signIn: found user with userId: xxXXxx")

                FirebaseCrashlytics.getInstance().apply {
                    setCustomKey(Analytics.KEY_FBASE_USER_ID, it.uid)
                    setCustomKey(Analytics.KEY_FBASE_IS_ANONYMOUS, it.isAnonymous)
                }

                analytics.updateUserProperty(it.uid, it.isAnonymous)
            }
        } else {
            Timber.d("signIn: user not found")
        }
    }

    private fun logSignUpEvent(user: FirebaseUser?, status: String, message: String?) {

        if (user == null) {

            analytics.logEvent(
                AuthEvent.signUp(
                    "mainFragment",
                    status,
                    "n/a",
                    false,
                    message ?: ""
                )
            )
        } else {
            analytics.logEvent(
                AuthEvent.signUp(
                    "mainFragment",
                    status,
                    user.uid,
                    user.isAnonymous,
                    message ?: ""
                )
            )
        }
    }

    private fun logLoginEvent(currentUser: FirebaseUser) {
        analytics.logEvent(
            AuthEvent.login(
                "mainFragment",
                currentUser.uid,
                currentUser.isAnonymous
            )
        )
    }

    private fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }


    override fun onSearchRequested(): Boolean {
        if (searchFragment.hasResults()) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            searchFragment.startRecognition()
        }
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
            when (keyCode) {

                KeyEvent.KEYCODE_BACK -> {
                    if (!isSystemKeyboardVisible()) {
                        onBackPressed()
                    }
                    return true
                }
            }
        return false
    }

    fun isSystemKeyboardVisible(): Boolean {
        return try {
            val manager: InputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val windowHeightMethod: Method =
                InputMethodManager::class.java.getMethod("getInputMethodWindowVisibleHeight")
            val height = windowHeightMethod.invoke(manager) as Int
            height > 0
        } catch (e: Exception) {
            false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
    fun isTV(): Boolean {
        return Build.MODEL.contains("AFT")
    }
}