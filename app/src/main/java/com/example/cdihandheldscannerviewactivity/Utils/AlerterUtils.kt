package com.example.cdihandheldscannerviewactivity.Utils

import android.app.Activity
import android.content.Context
import com.example.cdihandheldscannerviewactivity.R
import com.tapadoo.alerter.Alerter

class AlerterUtils {

    companion object{
        fun startAlert(activity:Activity, alertMessage: String, alertTitle:String, alertIcon: Int){
            Alerter.create(activity)
                .setTitle(alertTitle)
                .setText(alertMessage)
                .setIcon(alertIcon)
                .setBackgroundColorRes(R.color.CDI_Blue)
                .show()
        }
    }
}