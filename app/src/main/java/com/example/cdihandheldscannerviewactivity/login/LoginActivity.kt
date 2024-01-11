package com.example.cdihandheldscannerviewactivity.login

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.cdihandheldscannerviewactivity.MainActivity
import com.example.cdihandheldscannerviewactivity.Utils.Network.Company
import com.example.cdihandheldscannerviewactivity.Utils.Network.NetworkUtils
import com.example.cdihandheldscannerviewactivity.Utils.Network.ResponseWrapperUser
import com.example.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import com.example.cdihandheldscannerviewactivity.Utils.Network.User
import com.example.cdihandheldscannerviewactivity.Utils.Network.RequestUser
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.example.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.example.cdihandheldscannerviewactivity.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// This class represents the login activity for the application. It extends the AppCompatActivity class
class loginActivity : AppCompatActivity() {

    // Declare member variables.
    // These will be initialized later in the code.
    // ViewModel instance for the login activity
    private lateinit var viewModel: LoginViewModel
    // Button for login
    private lateinit var loginButton: Button
    // EditTexts for username and password inputs
    private lateinit var userNameEditTex: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    // Spinner for company selection
    private lateinit var companySpinner: Spinner
    // Root view of the layout
    private lateinit var rootView: View
    // Dialog for showing progress
    private lateinit var progressDialog: Dialog
    // Binding object for the login activity
    private lateinit var binding: ActivityLoginBinding

    // Object for managing network connectivity
    private lateinit var connectivityManager: ConnectivityManager
    // Callback for network connectivity changes
    private lateinit var networkCallback : NetworkCallback

    // Boolean to keep track of whether the app has been opened before
    private var hasAppBeenOpened: Boolean = false

    // This method is called when the activity is being created. It initializes the UI elements, ViewModel, Spinner, Observers, and Network Connection Handler.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityLoginBinding>(this,
            R.layout.activity_login
        )
        initUIElements()
        initViewModel()
        initSpinner()
        initObservers()
        initNetworkConnectionHandler()
    }

    // This method is called when the activity is being destroyed. It unregisters the network callback.
    override fun onDestroy() {
        super.onDestroy()
        // Unregister
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    // This method initializes the ViewModel and links it to the XML layout file.
    private fun initViewModel(){
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        // This links the view model with the XML layout file
        binding.loginViewModel = viewModel
        binding.lifecycleOwner = this
    }

    // This method initializes the network connection handler. It sets up a network request and a callback for handling changes in network connectivity.
    private fun initNetworkConnectionHandler(){
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder().build()
        networkCallback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Handle connection
                if (hasAppBeenOpened)
                    AlerterUtils.startInternetRestoredAlert(this@loginActivity)
                else
                    hasAppBeenOpened = true

                if(companySpinner.selectedItem == null) {
                    runOnUiThread {
                        progressDialog.show()
                        viewModel.getCompaniesFromBackendForSpinner()
                    }
                }
            }

            override fun onLost(network: Network) {
                // Handle disconnection
                hasAppBeenOpened = true
                AlerterUtils.startInternetLostAlert(this@loginActivity)
            }
        }
        // Register the Network Connection Handler
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    // This method initializes the UI elements. It sets up the login button, EditText fields, Spinner, root view, and progress dialog.
    private fun initUIElements(){
        loginButton = binding.loginButton
        loginButton.setOnClickListener{
            val selectedCompanyID = getSelectedCompanyIDFromCompanyName()
            viewModel.setUserValues(userNameEditTex.text.toString(), passwordEditText.text.toString(), selectedCompanyID)
            viewModel.verifyIfTooManyUsersConnected()
        }
        userNameEditTex = binding.usernameText
        passwordEditText = binding.passwordText
        companySpinner = binding.companySpinner
        rootView = binding.root // root view of the layout
        progressDialog = Dialog(this).apply{
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
            show()
        }
    }

    // This method initializes the observers for the ViewModel. It observes changes in the list of companies and the success status of the last API call.
    private fun initObservers(){
        viewModel.listOfCompanies.observe(this) { newCompaniesList ->
            progressDialog.dismiss()
            fillSpinnerWithCompanies(newCompaniesList)
        }

        viewModel.wasLastAPICallSuccessful.observe(this) {wasAPICallSuccessful ->
            if(!wasAPICallSuccessful){
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(this@loginActivity)
                Log.i("API Call", "API Call did not work")
            }
        }


        viewModel.hasTooManyUsersConnected.observe(this){hasTooManyUsersConnected ->
            if(!hasTooManyUsersConnected)
                login()
            else
                AlerterUtils.startErrorAlerter(this@loginActivity, "Too many users connected to app")
        }

        viewModel.canUserLogin.observe(this){ canUserLogin ->
            if(canUserLogin){
                progressDialog.dismiss()
                SharedPreferencesUtils.storeLoginInfoInSharedPref(viewModel.user.value!!.userName, viewModel.user.value!!.company, this@loginActivity)
                // This jumps from one Activity to another
                val intent = Intent(this@loginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                progressDialog.dismiss()
                AlerterUtils.startAlertWithColor(this@loginActivity,getString(R.string.login_declined), "Incorrect Credentials", R.drawable.circle_error_icon, android.R.color.holo_red_dark )
            }
        }
    }

    // This method populates the Spinner with a list of companies. It creates an ArrayAdapter with the company names and sets it as the adapter for the Spinner.
    private fun fillSpinnerWithCompanies(listOfCompanies: List<Company>) {
        // Create a mutable list of company names
        val companies = mutableListOf<String>()
        // Create an ArrayAdapter with the list of company names
        val adapter =
            ArrayAdapter(this@loginActivity, android.R.layout.simple_spinner_item, companies)
        // Set the layout for displaying the list of choices in the Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set the ArrayAdapter as the Spinner's adapter
        companySpinner.adapter = adapter
        // Add the company names to the list
        for (aCompany in listOfCompanies) {
            companies.add(aCompany.companyName)
        }
        // Notify the adapter that the data set has changed. This causes the Spinner to re-render its view.
        adapter.notifyDataSetChanged()
    }

    // This method initializes the Spinner. It sets up listeners for various events related to the Spinner.
    @SuppressLint("ClickableViewAccessibility")
    private fun initSpinner(){
        // Create a global layout listener. This is used to reset the Spinner's background when it closes.
        val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            // Reset background when spinner closes
            companySpinner.setBackgroundResource(R.drawable.white_drop_down)
            viewModel.isSpinnerArrowUp = false
        }

        // Set a touch listener for the Spinner. This is used to change the Spinner's background when it is clicked.
        companySpinner.setOnTouchListener{view, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    companySpinner.setBackgroundResource(R.drawable.white_drop_down_arrow_up)
                    companySpinner.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
                    viewModel.isSpinnerArrowUp = true
                }
            }
            false
        }

        // Set an item selected listener for the Spinner. This is used to reset the Spinner's background when an item is selected.
        companySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                companySpinner.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
                viewModel.isSpinnerArrowUp = false
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Set a touch listener for the root view. This is used to reset the Spinner's background when the user touches outside of the Spinner.
        rootView.setOnTouchListener { _, _ ->
            if (viewModel.isSpinnerArrowUp) {
                companySpinner.setBackgroundResource(R.drawable.white_drop_down)
                companySpinner.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
                viewModel.isSpinnerArrowUp = false
            }
            false
        }
    }

    private fun getSelectedCompanyIDFromCompanyName(): String{
        val selectedCompanyInSpinner: String = companySpinner.selectedItem.toString()
        lateinit var selectedCompanyID: String
        for (aCompany in viewModel.listOfCompanies.value!!) {
            if (selectedCompanyInSpinner == aCompany.companyName) {
                selectedCompanyID = aCompany.companyID
            }
        }
        return selectedCompanyID
    }

    // This method is called after the verification of the number of users is finished. It checks the network connection and the selected company, and then attempts to log in.
    private fun login() {
        progressDialog.show()
        if (!NetworkUtils.isDeviceOnline(this)) {
            progressDialog.dismiss()
            AlerterUtils.startInternetLostAlert(this)
        } else {
            if (companySpinner.selectedItem != null) {

                val requestBody = RequestUser(viewModel.user.value!!)
                viewModel.canLoginWithCredentials(requestBody)
            } else {
                progressDialog.dismiss()
                Toast.makeText(this, getString(R.string.company_not_chosen), Toast.LENGTH_SHORT).show()
            }

        }
    }
}



//      This code was used to preview the Body of the API Request
//        val moshi = Moshi.Builder()
//            .add(KotlinJsonAdapterFactory())
//            .build()
//
//        val jsonAdapter = moshi.adapter(requestUser::class.java)
//        val jsonString = jsonAdapter.toJson(requestBody)
//
//        // Now, you can log or inspect jsonString
//        println("JSON String - \n " + jsonString)
