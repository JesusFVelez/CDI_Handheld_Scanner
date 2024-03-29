package com.comdist.cdihandheldscannerviewactivity

import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.comdist.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.comdist.cdihandheldscannerviewactivity.databinding.ActivityMainBinding
import com.comdist.cdihandheldscannerviewactivity.login.LoginActivity

// Main activity class
class MainActivity : AppCompatActivity() {

    // Variables for binding, connectivity manager, network callback, and flag indicating if page has just started
    private lateinit var binding: ActivityMainBinding
    lateinit var connectivityManager: ConnectivityManager
    lateinit var networkCallback : ConnectivityManager.NetworkCallback
    private var hasPageJustStarted: Boolean = false

    // Method called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content view to the main activity layout
        @Suppress("UNUSED_VARIABLE")
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

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

                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
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
