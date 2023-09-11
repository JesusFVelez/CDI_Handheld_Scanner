package com.example.cdihandheldscannerviewactivity.login

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
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
import com.example.cdihandheldscannerviewactivity.databinding.ActivityLoginBinding
import com.example.cdihandheldscannerviewactivity.networkUtils.ResponseWrapperUser
import com.example.cdihandheldscannerviewactivity.networkUtils.ScannerAPI
import com.example.cdihandheldscannerviewactivity.networkUtils.User
import com.example.cdihandheldscannerviewactivity.networkUtils.requestUser
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.cdihandheldscannerviewactivity.MainActivity
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.networkUtils.Company

class loginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var loginButton: Button
    private lateinit var userNameEditTex: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var companySpinner: Spinner
    private lateinit var rootView: View

    private lateinit var progressDialog: Dialog


    private lateinit var binding: ActivityLoginBinding

//TODO (4) Hacer que cuando se haga el llamado al API que tenga un tipo de loading mechanism por si la internet del usuario no es instantanea
        // Note: Se puede hacer que cuando el observer note que llegue el valor nuevo, entonces que termine la animacion de loading
    // TODO (5) Se tiene que crear un tipo de mini bases de datos en la app (Con SQLITE o algo asi) para guardar el COMPANY y, possiblemente, el sign in info (esta temptativo porque no veo por que se tenga que tener el sign in info por ahora)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityLoginBinding>(this,
            R.layout.activity_login
        )

        loginButton = binding.loginButton
        userNameEditTex = binding.usernameText
        passwordEditText = binding.passwordText
        companySpinner = binding.companySpinner
        rootView = binding.root // root view of the layout
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding.loginViewModel = viewModel
        binding.lifecycleOwner = this


        initSpinner()


        progressDialog = Dialog(this).apply{
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
            show()
        }

        viewModel.listOfCompanies.observe(this) { newCompaniesList ->
            progressDialog.dismiss()
            fillSpinnerWithCompanies(newCompaniesList)
        }
        loginButton.setOnClickListener{
            loginButtonClickEvent(it)
        }

    }



    private fun fillSpinnerWithCompanies(listOfCompanies: List<Company>) {
        val companies = mutableListOf<String>()
        val adapter = ArrayAdapter(this@loginActivity, android.R.layout.simple_spinner_item, companies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        companySpinner.adapter = adapter
        for (aCompany in listOfCompanies) {
            companies.add(aCompany.companyName)
        }
        adapter.notifyDataSetChanged()
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


    // TODO consider if this needs to be moved to the ViewModel
    fun loginButtonClickEvent(view: View?) {
        println("Button Clicked")
        progressDialog.show()
        val selectedCompanyInSpinner: String = companySpinner.selectedItem.toString()
        lateinit var selectedCompanyID: String
        for(aCompany in viewModel.listOfCompanies.value!!){
            if (selectedCompanyInSpinner == aCompany.companyName){
                selectedCompanyID = aCompany.companyID
            }
        }
        val user = User(userNameEditTex.text.toString(), passwordEditText.text.toString(), selectedCompanyID)
        val requestBody = requestUser(user)

        ScannerAPI.retrofitService.isLogedIn(requestBody).enqueue(object : Callback<ResponseWrapperUser>{
            override fun onResponse(call: Call<ResponseWrapperUser>, response: Response<ResponseWrapperUser>) {
                if (response.body()?.response?.isSignedIn == true) {
                    Toast.makeText(this@loginActivity, "You have been logged in succesfully!", Toast.LENGTH_SHORT).show()
                    // This jumps from one Activity to another
                    progressDialog.dismiss()
                    val intent = Intent(this@loginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                else {
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
                Toast.makeText(this@loginActivity, "Something went wrong with the sign in", Toast.LENGTH_SHORT).show()
                println("Error -> " + t.message)
            }


        })

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
