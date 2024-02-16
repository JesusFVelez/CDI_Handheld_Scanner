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
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.cdihandheldscannerviewactivity.MainActivity
import com.example.cdihandheldscannerviewactivity.Utils.Network.Company
import com.example.cdihandheldscannerviewactivity.Utils.Network.NetworkUtils
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.example.cdihandheldscannerviewactivity.Utils.Network.WarehouseInfo
import com.example.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.example.cdihandheldscannerviewactivity.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputEditText


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
    private lateinit var companySpinner: AutoCompleteTextView
    // Spinner for warehouse selection
    private lateinit var warehouseSpinner: AutoCompleteTextView

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
        initViewModel()
        initUIElements()
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

                if(companySpinner.text.toString() == "") {
                    runOnUiThread {
                        progressDialog.show()
                        viewModel.getCompaniesFromBackendForSpinner()
                    }
                }
                if(warehouseSpinner.text.toString() == ""){
                    runOnUiThread{
                        progressDialog.show()
                        viewModel.getWarehousesFromBackendForSpinner()
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
            loginButtonClickEvent(it)
        }
        userNameEditTex = binding.usernameText
        passwordEditText = binding.passwordText
        // Init Company Spinner
        companySpinner = binding.companySpinner
        companySpinner.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && isFinishing) {
                // Show the dropdown menu when the AutoCompleteTextView gains focus
                (view as? AutoCompleteTextView)?.showDropDown()
            }
        }

        // Init Warehouse Spinner
        warehouseSpinner = binding.warehouseSpinner
        warehouseSpinner.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && isFinishing) {
                // Show the dropdown menu when the AutoCompleteTextView gains focus
                (view as? AutoCompleteTextView)?.showDropDown()
            }
        }




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

        viewModel.listOfWarehouses.observe(this) {newWarehousesList ->
            progressDialog.dismiss()
            fillSpinnerWithWarehouses(newWarehousesList)
        }

        viewModel.wasLastAPICallSuccessful.observe(this) {wasAPICallSuccessful ->
            if(!wasAPICallSuccessful){
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(this@loginActivity)
                Log.i("API Call", "API Call did not work")
            }
        }

        viewModel.isUserLoggedIn.observe(this){isUserLoggedIn ->
            if (isUserLoggedIn) {
                progressDialog.dismiss()
                SharedPreferencesUtils.storeLoginInfoInSharedPref(userNameEditTex.text.toString(), viewModel.currentlyChosenCompany.value!!.companyID, this@loginActivity)
                SharedPreferencesUtils.storeWarehouseInfoInSharedPref(viewModel.currentlyChosenWarehouse.value!!.warehouseName, viewModel.currentlyChosenWarehouse.value!!.warehouseNumber,this@loginActivity)
                // This jumps from one Activity to another
                val intent = Intent(this@loginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                progressDialog.dismiss()
                AlerterUtils.startAlertWithColor(this@loginActivity,getString(R.string.login_declined), "Incorrect Credentials", R.drawable.circle_error_icon, android.R.color.holo_red_dark )
            }

        }
    }

    // Populate Spinner with warehouse data
    private fun fillSpinnerWithWarehouses( newWarehouseList : List<WarehouseInfo>){
        val warehouses = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, warehouses)
        warehouseSpinner.setAdapter(adapter)
        for (aWarehouse in viewModel.listOfWarehouses.value!!) {
            warehouses.add(aWarehouse.warehouseName)
        }
        adapter.notifyDataSetChanged()
    }

    // This method populates the Spinner with a list of companies. It creates an ArrayAdapter with the company names and sets it as the adapter for the Spinner.
    private fun fillSpinnerWithCompanies(listOfCompanies: List<Company>) {
        // Create a mutable list of company names
        val companies = mutableListOf<String>()

        // Add the company names to the list
        for (aCompany in listOfCompanies) {
            companies.add(aCompany.companyName)
        }
        // Create an ArrayAdapter with the list of company names
        val adapter =
            ArrayAdapter(this@loginActivity,android.R.layout.simple_spinner_dropdown_item , companies)
        // Set the ArrayAdapter as the Spinner's adapter
        companySpinner.setAdapter(adapter)
        // Notify the adapter that the data set has changed. This causes the Spinner to re-render its view.
    }


    // This method is called when the login button is clicked. It checks the network connection and the selected company, and then attempts to log in.
    fun loginButtonClickEvent(view: View?) {
        progressDialog.show()

        if (!NetworkUtils.isDeviceOnline(this)) {
            progressDialog.dismiss()
            AlerterUtils.startInternetLostAlert(this)
        } else {
            if (companySpinner.text.toString() != "" && warehouseSpinner.text.toString() != "") {
                val selectedCompanyInSpinner: String = companySpinner.text.toString()
                val selectedWarehouseInSpinner: String = warehouseSpinner.text.toString()
                viewModel.setWarehouse(selectedWarehouseInSpinner)
                viewModel.setCompanyID(selectedCompanyInSpinner)
                viewModel.logUserIn(userNameEditTex.text.toString(), passwordEditText.text.toString())

            } else {
                progressDialog.dismiss()
                AlerterUtils.startErrorAlerter(this, getString(R.string.company_or_warehouse_not_found))
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
