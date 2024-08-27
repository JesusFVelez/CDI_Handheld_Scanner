package com.scannerapp.cdihandheldscannerviewactivity

import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.scannerapp.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.scannerapp.cdihandheldscannerviewactivity.databinding.ActivityMainBinding
import com.scannerapp.cdihandheldscannerviewactivity.login.LoginActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Main activity class
class MainActivity : AppCompatActivity() {

    // Variables for binding, connectivity manager, network callback, and flag indicating if page has just started
    private lateinit var binding: ActivityMainBinding
    lateinit var connectivityManager: ConnectivityManager
    lateinit var networkCallback : ConnectivityManager.NetworkCallback
    private var hasPageJustStarted: Boolean = false


    private var hasSessionTimedOut: Boolean = false
    private var isAppInForeGround: Boolean = false

    // Timeout after 15 minutes of inactivity
    val TIMEOUT_DURATION = 15 * 60 * 1000 // 1 minutes in milliseconds
    private var lastInteractionTime: Long = 0
    private val timeoutHandler = Handler(Looper.getMainLooper())
    private val timeoutRunnable = Runnable {
        // Handle session timeout here
        if(isAppInForeGround) {
           handleSessionTimeout()
        }
        else{
            hasSessionTimedOut = true
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        resetTimer()
    }


    override fun onPause() {
        super.onPause()
        isAppInForeGround = false
    }

    private fun resetTimer() {
        lastInteractionTime = System.currentTimeMillis()
        timeoutHandler.removeCallbacks(timeoutRunnable)
        timeoutHandler.postDelayed(timeoutRunnable, TIMEOUT_DURATION.toLong())
        hasSessionTimedOut = false
    }

    override fun onResume() {
        super.onResume()
        isAppInForeGround = true
        if(hasSessionTimedOut){
            handleSessionTimeout()
        }
    }

    private fun logoutUser(): Boolean {
        val companyID: String = SharedPreferencesUtils.getCompanyIDFromSharedPref(this)
        var hasUserBeenLoggedOut = false
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.i("Log out user" , "Error -> ${exception.message}")
            AlerterUtils.startNetworkErrorAlert(this@MainActivity)
        }
        lifecycleScope.launch(exceptionHandler) {
            try {
                ScannerAPI.getLoginService().logoutUser(companyID)
                // Log out and navigate to the login activity
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                hasUserBeenLoggedOut = true
                this@MainActivity.finish()
                startActivity(intent)


            }catch (e: Exception){
                Log.i("Log out user (e)", "Error -> ${e.message}")
                AlerterUtils.startNetworkErrorAlert(this@MainActivity)
            }

        }
        return hasUserBeenLoggedOut
    }


    private fun handleSessionTimeout(){
        Toast.makeText(this, "You have been signed out due to inactivity.", Toast.LENGTH_LONG)
            .show()
        logoutUser()
    }

    // Method called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view to the main activity layout
        @Suppress("UNUSED_VARIABLE")
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        resetTimer()

        // Set the toolbar
        val toolbar = findViewById<Toolbar>(R.id.custom_toolbar)
        setSupportActionBar(toolbar)


        // Set up the navigation controller
        val navController = this.findNavController(R.id.my_nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        // Initialize the connectivity manager and network request
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder().build()

        // Initialize the network callback to handle connection and disconnection events
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Handle connection
                if (hasPageJustStarted)
                    AlerterUtils.startInternetRestoredAlert(this@MainActivity)
                else
                    hasPageJustStarted = true
            }

            override fun onLost(network: Network) {
                // Handle disconnection
                hasPageJustStarted = true
                AlerterUtils.startInternetLostAlert(this@MainActivity)
            }
        }


        // Register the network callback
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);


    }

    // Method called when the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        // Unregister the network callback
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    // Method to handle navigation up action
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.my_nav_host_fragment)
        return navController.navigateUp()
    }


@Deprecated("Deprecated in Java")
override fun onBackPressed() {
    val navController = this.findNavController( R.id.my_nav_host_fragment)
//    if(navController.currentDestination?.id == R.id.orderPickingMainFragment) {
//        val currentFragment = navController.currentDestination
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as? NavHostFragment
//        val fragment = navHostFragment?.childFragmentManager?.fragments?.find { it is orderPickingMainFragment } as? orderPickingMainFragment
//        fragment?.let {
//            if (it.verifyIfOrderIsBeingPicked()) {
//                it.showErrorMessageWhenExitingScreenWithoutFinishingPicking()
//            } else
//                navController.navigateUp()
//            return
//        }
//    }
    if (navController.previousBackStackEntry != null) {
        // If there's something on the back stack, pop it
        navController.popBackStack()
    }else{
        // Otherwise, Ask the user if he wants to log out or not
        AlertDialog.Builder(this@MainActivity)
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                val hasUserBeenLoggedOut = logoutUser()
                if (hasUserBeenLoggedOut) super.onBackPressed()
            }
            .setNegativeButton("No", null)
            .show()
    }

}




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
           val navController = findNavController(R.id.my_nav_host_fragment)
            val currentFragment = navController.currentDestination
//            if(currentFragment?.id == R.id.orderPickingMainFragment){
//                val navHostFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as? NavHostFragment
//                val fragment = navHostFragment?.childFragmentManager?.fragments?.find { it is orderPickingMainFragment } as? orderPickingMainFragment
//                fragment?.let{
//                    if (it.verifyIfOrderIsBeingPicked()) {
//                        it.showErrorMessageWhenExitingScreenWithoutFinishingPicking()
//                    } else
//                        navController.navigateUp()
//                    return true
//                }
//            }
        }
        return super.onOptionsItemSelected(item)
    }



}
