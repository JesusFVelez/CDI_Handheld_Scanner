package com.example.cdihandheldscannerviewactivity.Utils.Network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.cdihandheldscannerviewactivity.Utils.AlerterUtils

// Class for network related utilities
class NetworkUtils() {

    companion object {


        // Method to check if the device is connected to the internet
        // Returns true if the device is online, false otherwise
        fun isDeviceOnline(context: Context): Boolean {
            // Get the ConnectivityManager instance
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            // Get the network capabilities of the active network
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            // If network capabilities is null, then the device is offline
            // Otherwise, the device is online
            return if (networkCapabilities == null) {
                Log.d("tagLog", "Device Offline")
                false
            } else {
                Log.d("tagLog", "Device Online")
                true
//                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
//                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
//                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED)
            }
        }
    }
}





