package com.comdist.cdihandheldscannerviewactivity.SetNetworkDetails

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.ConnectionTestingWrapper
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import com.comdist.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.comdist.cdihandheldscannerviewactivity.databinding.ActivityNetworkDetailsBinding
import com.comdist.cdihandheldscannerviewactivity.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNetworkDetailsBinding
    private lateinit var ipAddressEditText: EditText
    private lateinit var portNumberEditText: EditText
    private lateinit var testConnectionButton: Button
    private lateinit var viewModel: NetworkDetailsViewModel
    // Dialog for showing progress
    private lateinit var progressDialog: Dialog

    private var hasConnectionBeenTested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityNetworkDetailsBinding>(this,
            R.layout.activity_network_details
        )
        if(haveIPandPortBeenEnteredAlready()){
            enterIPandPortNumbersToScannerAPI()
            jumpToLoginActivity()
        }
        initUIElements()
        viewModel = ViewModelProvider(this)[NetworkDetailsViewModel::class.java]
        initObservers()
    }

    private fun enterIPandPortNumbersToScannerAPI(){
        val ip = SharedPreferencesUtils.getIPAddressFromSharedPref(this@NetworkDetailsActivity)
        val port = SharedPreferencesUtils.getPortNumberFromSharedPref(this@NetworkDetailsActivity)
        ScannerAPI.setIpAddressAndPortNumber(ip!!, port!!)
    }


    private fun haveIPandPortBeenEnteredAlready():Boolean{
        val ip = SharedPreferencesUtils.getIPAddressFromSharedPref(this@NetworkDetailsActivity)
        val port = SharedPreferencesUtils.getPortNumberFromSharedPref(this@NetworkDetailsActivity)
        return ip != "N/A" && port != "N/A"
    }

    private fun jumpToLoginActivity(){
        // This jumps from one Activity to another
        val intent = Intent(this@NetworkDetailsActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initUIElements(){
        ipAddressEditText = binding.ipAddressText
        portNumberEditText = binding.portNumberText
        testConnectionButton = binding.testConnectionButton
        testConnectionButton.setOnClickListener{
                val ipAddress = ipAddressEditText.text.toString()
                val portNumber = portNumberEditText.text.toString()
                if(ipAddress != "" && portNumber != "") {
                    hasConnectionBeenTested = true
                    progressDialog.show()
                    verifyBackendConnection(ipAddress, portNumber)
                }
                else
                    AlerterUtils.startErrorAlerter(this,"IP Address or Port Number was left empty.")
        }
        progressDialog = PopupWindowUtils.getLoadingPopup(this)
    }

    private fun verifyBackendConnection(ipAddress:String, portNumber:String){
        ScannerAPI.setIpAddressAndPortNumber(ipAddress, portNumber)
        ScannerAPI.getLoginService().testConnection()
            .enqueue(object : Callback<ConnectionTestingWrapper> {
                override fun onResponse(
                    call: Call<ConnectionTestingWrapper>,
                    response: Response<ConnectionTestingWrapper>
                ) {
                    if (response.body()?.response?.hasConnectionSucceeded == true) {
                        progressDialog.dismiss()
                        SharedPreferencesUtils.storeIPAndPortInSharedPref(ipAddress, portNumber, this@NetworkDetailsActivity)
                        jumpToLoginActivity()
                    } else {
                        progressDialog.dismiss()
                        AlerterUtils.startAlertWithColor(this@NetworkDetailsActivity,getString(R.string.login_declined), "Incorrect Credentials", R.drawable.circle_error_icon, android.R.color.holo_red_dark )
                    }
                }

                override fun onFailure(call: Call<ConnectionTestingWrapper>, t: Throwable) {
                    progressDialog.dismiss()
                    AlerterUtils.startErrorAlerter(this@NetworkDetailsActivity, "Unsuccessful Connection \n Check IP and Port \n or \n Check if Server is enabled")
                    println("Error -> " + t.message)
                }


            })
    }

    private fun initObservers(){
        viewModel.hasConnectionToBackendSucceeded.observe(this){ wasBackendConnectionSuccessful ->
            if(wasBackendConnectionSuccessful && hasConnectionBeenTested){
                // This jumps from one Activity to another
                val intent = Intent(this@NetworkDetailsActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }else if(hasConnectionBeenTested)
                AlerterUtils.startErrorAlerter(this, "Connection Unsuccessful \n Verify IP and Port")

            progressDialog.dismiss()
        }
        viewModel.wasLastAPICallSuccessful.observe(this){wasLastAPICallSuccessful ->
            if(!wasLastAPICallSuccessful){
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(this@NetworkDetailsActivity)
                Log.i("API Call", "API Call did not work")
            }
        }

    }
}