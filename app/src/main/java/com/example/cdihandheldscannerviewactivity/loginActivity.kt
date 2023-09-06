package com.example.cdihandheldscannerviewactivity

import android.annotation.SuppressLint
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
import com.example.cdihandheldscannerviewactivity.databinding.ActivityLoginBinding
import com.example.cdihandheldscannerviewactivity.network.Company
import com.example.cdihandheldscannerviewactivity.network.ResponseWrapper
import com.example.cdihandheldscannerviewactivity.network.ResponseWrapperUser
import com.example.cdihandheldscannerviewactivity.network.ScannerAPI
import com.example.cdihandheldscannerviewactivity.network.User
import com.example.cdihandheldscannerviewactivity.network.requestUser
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class loginActivity : AppCompatActivity() {


    private lateinit var loginButton: Button
    private lateinit var userNameEditTex: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var companySpinner: Spinner
    private lateinit var rootView: View

    private lateinit var listOfCompanies: List<Company>
    private var isSpinnerArrowUp: Boolean = false
    private lateinit var binding: ActivityLoginBinding

//TODO (4) Hacer que cuando se haga el llamado al API que tenga un tipo de loading mechanism por si la internet del usuario no es instantanea
    // TODO (5) Se tiene que crear un tipo de mini bases de datos en la app (Con SQLITE o algo asi) para guardar el COMPANY y, possiblemente, el sign in info (esta temptativo porque no veo por que se tenga que tener el sign in info por ahora)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityLoginBinding>(this,R.layout.activity_login)

        loginButton = binding.loginButton
        loginButton.setOnClickListener{
            loginButtonClickEvent(it)
        }
        userNameEditTex = binding.usernameText
        passwordEditText = binding.passwordText
        companySpinner = binding.companySpinner
        rootView = binding.root // root view of the layout


        getCompaniesFromBackendForSpinner()
        initSpinner()
    }



    private fun fillSpinnerWithCompanies() {
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

    private fun getCompaniesFromBackendForSpinner() {
        ScannerAPI.retrofitService.getCompanies().enqueue(object : Callback<ResponseWrapper> {
            override fun onResponse(
                call: Call<ResponseWrapper>,
                response: Response<ResponseWrapper>
            ) {
                println(
                    "Response" + (response.body()?.response?.companies?.companies?.get(0)
                        .toString())
                )
                listOfCompanies = response.body()!!.response.companies.companies
                fillSpinnerWithCompanies()
            }

            override fun onFailure(call: Call<ResponseWrapper>, t: Throwable) {
                println("Something failed: \n " + t.message)
                Toast.makeText(this@loginActivity, "There has been an error with the connection", Toast.LENGTH_SHORT).show()
            }
        })
    }



    @SuppressLint("ClickableViewAccessibility")
    private fun initSpinner(){

        val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            // Reset background when spinner closes
            companySpinner.setBackgroundResource(R.drawable.drop_down_background)
            isSpinnerArrowUp = false
        }

        // set what happens whenever the Spinner is clicked
        companySpinner.setOnTouchListener{view, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    companySpinner.setBackgroundResource(R.drawable.drop_down_arrow_up)
                    companySpinner.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
                    isSpinnerArrowUp = true
                }
            }
            false
        }

        // set what happens whenever an item is clicked in the spinner
        companySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                companySpinner.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
                isSpinnerArrowUp = false
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Reset the background when the user touches outside of the Spinner
        rootView.setOnTouchListener { _, _ ->
            if (isSpinnerArrowUp) {
                companySpinner.setBackgroundResource(R.drawable.drop_down_background)
                companySpinner.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
                isSpinnerArrowUp = false
            }
            false
        }
    }

    fun loginButtonClickEvent(view: View?) {
        println("Button Clicked")
        val selectedCompanyInSpinner: String = companySpinner.selectedItem.toString()
        lateinit var selectedCompanyID: String
        for(aCompany in listOfCompanies){
            if (selectedCompanyInSpinner == aCompany.companyName){
                selectedCompanyID = aCompany.companyID
            }
        }


        val user = User(userNameEditTex.text.toString(), passwordEditText.text.toString(), selectedCompanyID)
        val requestBody = requestUser(user)

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

        ScannerAPI.retrofitService.isLogedIn(requestBody).enqueue(object : Callback<ResponseWrapperUser>{
            override fun onResponse(call: Call<ResponseWrapperUser>, response: Response<ResponseWrapperUser>) {
                if (response.body()?.response?.isSignedIn == true) {
                    Toast.makeText(this@loginActivity, "You have been logged in succesfully!", Toast.LENGTH_SHORT).show()
                    // This jumps from one Activity to another
                    val intent = Intent(this@loginActivity, MainActivity::class.java)
                    startActivity(intent)


                }
                else
                    Toast.makeText(this@loginActivity, "Your credentials are wrong, try again!" , Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<ResponseWrapperUser>, t: Throwable) {
                Toast.makeText(this@loginActivity, "Something went wrong with the sign in", Toast.LENGTH_SHORT).show()
                println("Error -> " + t.message)
            }


        })



    }


}