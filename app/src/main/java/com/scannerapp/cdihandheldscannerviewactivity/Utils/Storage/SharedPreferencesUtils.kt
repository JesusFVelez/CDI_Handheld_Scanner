package com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

// Class for handling shared preferences for storing and retrieving user login information
class SharedPreferencesUtils {

    companion object{
        // Constants for shared preferences keys
        private const val companyIDSharedPrefKey: String = "companyId"
        private const val usernameSharedPrefKey: String = "username"
        private const val userLoginSharedPrefName: String = "userLoginInfo"
        private const val IPPortSharedPrefName: String = "IPAndPort"
        private const val IPSharedPrefKey:String = "IP"
        private const val portNumSharedPrefKey: String = "PortNumber"

        private const val warehouseInfoSharedPrefName: String = "WarehouseInfo"
        private const val warehouseNameSharedPrefKey: String = "WarehouseName"
        private const val warehouseNumberSharedPrefKey: String = "WarehouseNumber"


        // Method for storing user login information in shared preferences
        fun storeLoginInfoInSharedPref(username: String, companyID: String, context: Context){
            // Get the shared preferences editor
            val sharedPref = context.getSharedPreferences(
                userLoginSharedPrefName,
                AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            // Store the username and company ID in shared preferences
            editor.putString(usernameSharedPrefKey, username)
            editor.putString(companyIDSharedPrefKey, companyID)
            // Apply the changes to the shared preferences
            editor.apply()
        }

        // Method for storing user warehouse information in shared preferences
        fun storeWarehouseInfoInSharedPref(warehouseName: String, warehouseNumber: Int, context: Context){
            // Get the shared preferences editor
            val sharedPref = context.getSharedPreferences(
                warehouseInfoSharedPrefName,
                AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            // Store the username and company ID in shared preferences
            editor.putString(warehouseNameSharedPrefKey, warehouseName)
            editor.putInt(warehouseNumberSharedPrefKey, warehouseNumber)
            // Apply the changes to the shared preferences
            editor.apply()
        }


        fun storeAStringInASharedPref(sharedPrefName:String, sharedPrefKey: String, sharedPrefValue: String, context: Context){
            val sharedPref = context.getSharedPreferences(sharedPrefName, AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString(sharedPrefKey, sharedPrefValue)
            editor.apply()
        }


        // Method for storing user login information in shared preferences
        fun storeIPAndPortInSharedPref(IP: String, port: String, context: Context){
            // Get the shared preferences editor
            val sharedPref = context.getSharedPreferences(
                IPPortSharedPrefName,
                AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            // Store the username and company ID in shared preferences
            editor.putString(IPSharedPrefKey, IP)
            editor.putString(portNumSharedPrefKey, port)
            // Apply the changes to the shared preferences
            editor.apply()
        }

        fun getIPAddressFromSharedPref(context: Context): String? {
            // Get the shared preferences
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(IPPortSharedPrefName, Context.MODE_PRIVATE)
            // Retrieve the ip address from shared preferences
            return sharedPref.getString(IPSharedPrefKey, "N/A")
        }

        fun getWarehouseNameFromSharedPref(context: Context): String? {
            // Get the shared preferences
            val sharedPref: SharedPreferences = context.getSharedPreferences(
                warehouseInfoSharedPrefName, Context.MODE_PRIVATE
            )
            // Retrieve the ip address from shared preferences
            return sharedPref.getString(warehouseNameSharedPrefKey, "N/A")
        }

        fun getWarehouseNumberFromSharedPref(context: Context): Int {
            // Get the shared preferences
            val sharedPref: SharedPreferences = context.getSharedPreferences(
                warehouseInfoSharedPrefName, Context.MODE_PRIVATE
            )
            // Retrieve the ip address from shared preferences
            return sharedPref.getInt(warehouseNumberSharedPrefKey, 0)
        }


        fun getPortNumberFromSharedPref(context: Context): String? {
            // Get the shared preferences
            val sharedPref: SharedPreferences =
                context.getSharedPreferences(IPPortSharedPrefName, Context.MODE_PRIVATE)
            // Retrieve the port number from shared preferences
            return sharedPref.getString(portNumSharedPrefKey, "N/A")
        }

        // Method for retrieving the username from shared preferences
        fun getUserNameFromSharedPref(context: Context): String {
            // Get the shared preferences
            val sharedPref: SharedPreferences = context.getSharedPreferences(userLoginSharedPrefName, Context.MODE_PRIVATE)
            // Retrieve the username from shared preferences
            val username = sharedPref.getString(usernameSharedPrefKey, "N/A")
            return username!!
        }

        // Method for retrieving the company ID from shared preferences
        fun getCompanyIDFromSharedPref(context: Context): String {
            // Get the shared preferences
            val sharedPref: SharedPreferences = context.getSharedPreferences(userLoginSharedPrefName, Context.MODE_PRIVATE)
            // Retrieve the company ID from shared preferences
            val companyID = sharedPref.getString(companyIDSharedPrefKey, "N/A")
            return companyID!!
        }
    }
}
