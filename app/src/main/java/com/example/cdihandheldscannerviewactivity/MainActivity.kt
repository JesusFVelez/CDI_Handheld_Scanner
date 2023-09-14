package com.example.cdihandheldscannerviewactivity
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.example.cdihandheldscannerviewactivity.databinding.ActivityMainBinding
import com.example.cdihandheldscannerviewactivity.login.loginActivity


class MainActivity : AppCompatActivity() {

//TODO(1) falta añadirle animations a cuando se cambia de fragment

    // TODO(2) falta anñadirle el Burger Menu al home screen for visual purposes (despues se le dara funcionalidad un poco mas pensada

private lateinit var binding: ActivityMainBinding
private lateinit var connectivityManager: ConnectivityManager
private lateinit var networkCallback : ConnectivityManager.NetworkCallback
private var hasAppBeenOpened: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.custom_toolbar)
        setSupportActionBar(toolbar)

        val navController = this.findNavController(R.id.my_nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (this@MainActivity.findNavController(R.id.my_nav_host_fragment).currentDestination?.id == R.id.homeScreenFragment) {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Log Out")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes") { _, _ ->

                            val intent = Intent(this@MainActivity, loginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
            }
        })


        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder().build()
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Handle connection
                if (hasAppBeenOpened)
                    Toast.makeText(this@MainActivity, "Wifi connectivity restored", Toast.LENGTH_SHORT).show()
                else
                    hasAppBeenOpened = false
            }

            override fun onLost(network: Network) {
                // Handle disconnection
                hasAppBeenOpened = true
                Toast.makeText(this@MainActivity, "Wifi connectivity lost", Toast.LENGTH_SHORT).show()
            }
        }

        // Register
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }


    override fun onDestroy() {
        super.onDestroy()
        // Unregister
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.my_nav_host_fragment)
        return navController.navigateUp()
    }

}