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
import com.example.cdihandheldscannerviewactivity.Network.Company
import com.example.cdihandheldscannerviewactivity.Network.NetworkUtils
import com.example.cdihandheldscannerviewactivity.Network.ResponseWrapperUser
import com.example.cdihandheldscannerviewactivity.Network.ScannerAPI
import com.example.cdihandheldscannerviewactivity.Network.User
import com.example.cdihandheldscannerviewactivity.Network.requestUser
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Storage.SharedPreferencesUtils
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
//                    Alerter.create(this)
//                        .setTitle(R.string.alert_title)
//                        .setText(R.string.alert_text)
//                        .show()
                    Toast.makeText(this@loginActivity, resources.getString(R.string.internet_restored), Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@loginActivity, resources.getString(R.string.internet_lost), Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, resources.getString(R.string.network_request_error_message), Toast.LENGTH_SHORT).show()
                Log.i("API Call", "API Call did not work")
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
            companySpinner.setBackgroundResource(R.drawable.drop_down_background)
            viewModel.isSpinnerArrowUp = false
        }

        // Set a touch listener for the Spinner. This is used to change the Spinner's background when it is clicked.
        companySpinner.setOnTouchListener{view, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    companySpinner.setBackgroundResource(R.drawable.drop_down_arrow_up)
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
                companySpinner.setBackgroundResource(R.drawable.drop_down_background)
                companySpinner.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
                viewModel.isSpinnerArrowUp = false
            }
            false
        }
    }

    // This method is called when the login button is clicked. It checks the network connection and the selected company, and then attempts to log in.
    fun loginButtonClickEvent(view: View?) {

        println("Button Clicked")
        progressDialog.show()

        if (!NetworkUtils.isDeviceOnline(this)) {
            progressDialog.dismiss()
            Toast.makeText(
                this,
                resources.getString(R.string.no_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            if (companySpinner.selectedItem != null) {
                val selectedCompanyInSpinner: String = companySpinner.selectedItem.toString()

                lateinit var selectedCompanyID: String
                for (aCompany in viewModel.listOfCompanies.value!!) {
                    if (selectedCompanyInSpinner == aCompany.companyName) {
                        selectedCompanyID = aCompany.companyID
                    }
                }

                val user = User(
                    userNameEditTex.text.toString(),
                    passwordEditText.text.toString(),
                    selectedCompanyID
                )
                val requestBody = requestUser(user)

                ScannerAPI.retrofitService.isLogedIn(requestBody)
                    .enqueue(object : Callback<ResponseWrapperUser> {
                        override fun onResponse(
                            call: Call<ResponseWrapperUser>,
                            response: Response<ResponseWrapperUser>
                        ) {
                            if (response.body()?.response?.isSignedIn == true) {
                                Toast.makeText(
                                    this@loginActivity,
                                    getString(R.string.succesfull_login),
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressDialog.dismiss()
                                SharedPreferencesUtils.storeLoginInfoInSharedPref(user.userName, selectedCompanyID, this@loginActivity)
                                // This jumps from one Activity to another
                                val intent = Intent(this@loginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                progressDialog.dismiss()
                                Toast.makeText(
                                    this@loginActivity,
                                    getString(R.string.login_declined),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                            override fun onFailure(call: Call<ResponseWrapperUser>, t: Throwable) {
                            progressDialog.dismiss()
                            Toast.makeText(
                                this@loginActivity,
                                resources.getString(R.string.network_request_error_message),
                                Toast.LENGTH_SHORT
                            ).show()
                            println("Error -> " + t.message)
                        }


                    })
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
