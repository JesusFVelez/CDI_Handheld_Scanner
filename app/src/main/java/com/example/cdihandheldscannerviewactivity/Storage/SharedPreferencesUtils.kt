package com.example.cdihandheldscannerviewactivity.Storage

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

        // Method for storing user login information in shared preferences
        fun storeLoginInfoInSharedPref(username: String, companyID: String, context: Context){
            // Get the shared preferences editor
            val sharedPref = context.getSharedPreferences(userLoginSharedPrefName,
                AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            // Store the username and company ID in shared preferences
            editor.putString(usernameSharedPrefKey, username)
            editor.putString(companyIDSharedPrefKey, companyID)
            // Apply the changes to the shared preferences
            editor.apply()
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
