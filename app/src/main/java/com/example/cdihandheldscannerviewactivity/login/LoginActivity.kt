package com.example.cdihandheldscannerviewactivity.login

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
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
import com.example.cdihandheldscannerviewactivity.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class loginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var loginButton: Button
    private lateinit var userNameEditTex: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var companySpinner: Spinner
    private lateinit var rootView: View
    private lateinit var progressDialog: Dialog
    private lateinit var binding: ActivityLoginBinding

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback : NetworkCallback

    private var hasAppBeenOpened: Boolean = false

    // TODO (5) Se tiene que crear un tipo de mini bases de datos en la app (Con SQLITE o algo asi) para guardar el COMPANY y, possiblemente, el sign in info (esta temptativo porque no veo por que se tenga que tener el sign in info por ahora)
    // TODO - Despues, cojer un tiempo para manejar los errores de network
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityLoginBinding>(this,
            R.layout.activity_login
        )
        initUIElements()
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding.loginViewModel = viewModel
        binding.lifecycleOwner = this
        initSpinner()
        initObservers()

        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder().build()
        networkCallback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Handle connection
                if (hasAppBeenOpened)
                    Toast.makeText(this@loginActivity, "Wifi connectivity restored", Toast.LENGTH_SHORT).show()
                else
                    hasAppBeenOpened = false

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
                Toast.makeText(this@loginActivity, "Wifi connectivity lost", Toast.LENGTH_SHORT).show()
            }
        }

        // Register
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);





    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }


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


    private fun initObservers(){
        viewModel.listOfCompanies.observe(this) { newCompaniesList ->
            progressDialog.dismiss()
            fillSpinnerWithCompanies(newCompaniesList)
        }

        viewModel.wasLastAPICallSuccessful.observe(this) {wasAPICallSuccessful ->
            if(!wasAPICallSuccessful){
                progressDialog.dismiss()
                Toast.makeText(this, resources.getString(R.string.network_request_error_message), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun fillSpinnerWithCompanies(listOfCompanies: List<Company>) {
        runOnUiThread {
            val companies = mutableListOf<String>()
            val adapter =
                ArrayAdapter(this@loginActivity, android.R.layout.simple_spinner_item, companies)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            companySpinner.adapter = adapter
            for (aCompany in listOfCompanies) {
                companies.add(aCompany.companyName)
            }
            adapter.notifyDataSetChanged()
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initSpinner(){

        val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            // Reset background when spinner closes
            companySpinner.setBackgroundResource(R.drawable.drop_down_background)
            viewModel.isSpinnerArrowUp = false
        }

        // set what happens whenever the Spinner is clicked
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

        // set what happens whenever an item is clicked in the spinner
        companySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                companySpinner.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
                viewModel.isSpinnerArrowUp = false
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Reset the background when the user touches outside of the Spinner
        rootView.setOnTouchListener { _, _ ->
            if (viewModel.isSpinnerArrowUp) {
                companySpinner.setBackgroundResource(R.drawable.drop_down_background)
                companySpinner.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
                viewModel.isSpinnerArrowUp = false
            }
            false
        }
    }


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
                                    "You have been logged in succesfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // This jumps from one Activity to another
                                progressDialog.dismiss()

                                val intent = Intent(this@loginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                progressDialog.dismiss()
                                Toast.makeText(
                                    this@loginActivity,
                                    "Your credentials are wrong, try again!",
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
                Toast.makeText(this, "Warehouse not chosen", Toast.LENGTH_SHORT).show()
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
