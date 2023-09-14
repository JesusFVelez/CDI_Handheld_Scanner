package com.example.cdihandheldscannerviewactivity.Storage

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.example.cdihandheldscannerviewactivity.R


class SharedPreferencesUtils {

    companion object{
        private const val companyIDSharedPrefID: String = "companyId"
        private const val usernameSharedPrefID: String = "username"
        private const val userLoginSharedPrefName: String = "userLoginInfo"

        fun storeLoginInfoInSharedPref(username: String, companyID: String, context: Context){
            val sharedPref = context.getSharedPreferences(SharedPreferencesUtils.userLoginSharedPrefName,
                AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            editor.putString(usernameSharedPrefID, username)
            editor.putString(companyIDSharedPrefID, companyID)
            editor.apply()
        }

        fun getUserNameFromSharedPref(context: Context): String {
            val sharedPref: SharedPreferences = context.getSharedPreferences(userLoginSharedPrefName, Context.MODE_PRIVATE)
            val username = sharedPref.getString(usernameSharedPrefID, "N/A")
            return username!!
        }

        fun getCompanyIDFromSharedPref(context: Context): String {
            val sharedPref: SharedPreferences = context.getSharedPreferences(userLoginSharedPrefName, Context.MODE_PRIVATE)
            val companyID = sharedPref.getString(companyIDSharedPrefID, "N/A")
            return companyID!!
        }


    }
}