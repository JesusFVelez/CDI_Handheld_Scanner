package com.scannerapp.cdihandheldscannerviewactivity.Utils

import android.app.Activity
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.tapadoo.alerter.Alerter

class AlerterUtils {

    companion object{


        fun startRegularAlert(activity:Activity, alertMessage: String, alertTitle:String, alertIcon: Int){
            Alerter.create(activity)
                .setTitle(alertTitle)
                .setText(alertMessage)
                .setIcon(alertIcon)
                .setBackgroundColorRes(R.color.CDI_Blue)
                .show()
        }

        fun startAlertWithColor(activity:Activity, alertMessage: String, alertTitle:String, alertIcon: Int, color: Int){
            Alerter.create(activity)
                .setTitle(alertTitle)
                .setText(alertMessage)
                .setIcon(alertIcon)
                .setBackgroundColorRes(color)
                .show()
        }

        fun startWarningAlerter(activity: Activity, warningMessage:String){
            Alerter.create(activity)
                .setTitle("Warning")
                .setText("${warningMessage} \n \nTap to dismiss.")
                .setIcon(R.drawable.black_warning_icon)
                .setIconColorFilter(0)
                .setBackgroundColorRes(R.color.CDI_Yellow)
                .setTextAppearance(R.style.WarningText)
                .setTitleAppearance(R.style.WarningText)
                .enableInfiniteDuration(true)
                .show()
        }


        fun startErrorAlerter(activity: Activity, errorMessage:String){
            Alerter.create(activity)
                .setTitle("Error")
                .setText("${errorMessage} \n \nTap to dismiss.")
                .setIcon(R.drawable.circle_error_icon)
                .setBackgroundColorRes(android.R.color.holo_red_dark)
                .enableInfiniteDuration(true)
                .show()
        }

        fun startInternetLostAlert(activity: Activity){
            Alerter.create(activity)
                .setTitle(activity.resources.getString(R.string.internet_lost_title))
                .setText(activity.resources.getString(R.string.internet_lost))
                .setIcon(R.drawable.wifi_disconnected)
                .setBackgroundColorRes(R.color.CDI_Blue)
                .show()
        }

        fun startInternetRestoredAlert(activity: Activity){
            Alerter.create(activity)
                .setTitle(activity.resources.getString(R.string.internet_restored_title))
                .setText(activity.resources.getString(R.string.internet_restored))
                .setIcon(R.drawable.wifi_conected)
                .setBackgroundColorRes(R.color.CDI_Blue)
                .show()
        }

        fun startNetworkErrorAlert(activity: Activity, networkErrorMessage: String){
            Alerter.create(activity)
                .setTitle(activity.resources.getString(R.string.network_request_error_message))
                .setText(networkErrorMessage)
                .setIcon(R.drawable.network_error)
                .setBackgroundColorRes(R.color.CDI_Blue)
                .show()
        }

        fun startSuccessAlert(activity: Activity, successTitle: String, successMessage: String){
            Alerter.create(activity)
                .setTitle(successTitle)
                .setText(successMessage)
                .setIcon(R.drawable.success_icon)
                .setBackgroundColorRes(R.color.success_green)
                .show()
        }
    }
}