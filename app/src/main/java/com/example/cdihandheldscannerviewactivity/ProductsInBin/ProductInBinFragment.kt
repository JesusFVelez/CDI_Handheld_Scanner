package com.example.cdihandheldscannerviewactivity.ProductsInBin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.opengl.Visibility
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdihandheldscannerviewactivity.Utils.Network.WarehouseInfo
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.example.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.example.cdihandheldscannerviewactivity.databinding.FragmentProductInBinBinding
import android.view.ViewGroup.LayoutParams
import android.view.animation.AnimationUtils
import com.example.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.example.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.tapadoo.alerter.Alerter


//TODO - Figure out if there is a way to blur the background behind the Popup window so that it does not look as bad
class ProductInBinFragment : Fragment(), ProductsInBinItemOnClickListener{

    // Declare UI elements and ViewModel
    private lateinit var binding: FragmentProductInBinBinding
    private lateinit var binNumberEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var numberOfItemsTextView: TextView
    private val viewModel: ProductsInBinViewModel by activityViewModels()
    private lateinit var adapter : ProductsInBinAdapter
    private lateinit var progressDialog: Dialog

    private var hasSearchButtonBeenPressed: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // TODO - Anñadir funcionalidad que haga que el formato del editText del Bin Number siempre tenga los guiones entremedio (ver commentarios en Figma)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize data binding and inflate the layout
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_in_bin, container, false)
//        viewModel = ViewModelProvider(this)[ProductsInBinViewModel::class.java]
        // Using the LiveData with the UI is not working so I commented it
//        binding.productsInBinViewModel = viewModel
//        binding.lifecycleOwner = this

        // Initialize UI elements, Spinner, and RecyclerView adapter
        initUIElements()
        adapter = ProductsInBinAdapter(this)
        binding.productsInBinList.adapter = adapter

        // Initialize LiveData observers
        initObservers()



        // Retrieve company ID from shared preferences
        val companyID:String = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        viewModel.setCompanyIDFromSharedPref(companyID)

        // Retrieve warehouse number from shared preferences
        val warehouseNumber:Int = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
        viewModel.setWarehouseNumberFromSharedPref(warehouseNumber)

        return binding.root
    }

    // Initialize UI elements
    private fun initUIElements(){
        binNumberEditText = binding.binNumberEditText
        binNumberEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                // Handle the Enter key press here
                progressDialog.show()
                viewModel.getProductInfoFromBackend(binNumberEditText.text.toString())
                true
            } else {
                false
            }
        }
        searchButton = binding.searchBinButton
        searchButton.setOnClickListener{
            // here goes the API Call for the filling of the recycler view
            progressDialog.show()
            hasSearchButtonBeenPressed = true
            viewModel.getProductInfoFromBackend(binNumberEditText.text.toString())
        }
        numberOfItemsTextView = binding.totalProductsTextView
        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())
        binding.productsInBinList.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        binNumberEditText.requestFocus()
    }



    // Handle onResume lifecycle event
    override fun onResume() {
        super.onResume()
        val bundle = arguments
        val lastFragmentName : String = BundleUtils.getPastFragmentNameFromBundle(bundle)
        if(lastFragmentName == "HomeScreen"){
            viewModel.clearListOfProducts()
            bundle?.clear()
        }
    }


    // Handle onPause lifecycle event
    override fun onPause() {
        super.onPause()


    }

    // Initialize LiveData observers
    private fun initObservers(){

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner){wasAPICallSuccessful ->
            if(!wasAPICallSuccessful && hasSearchButtonBeenPressed){
                progressDialog.dismiss()
                hasSearchButtonBeenPressed = false
                AlerterUtils.startNetworkErrorAlert(requireActivity())
            }
        }

        viewModel.numberOfItemsInBin.observe(viewLifecycleOwner){newNumberOfItems ->
            if (newNumberOfItems > 1 || newNumberOfItems == 0)
                numberOfItemsTextView.text = "${newNumberOfItems.toString()} items in Bin"
            else
                numberOfItemsTextView.text = "${newNumberOfItems.toString()} item in Bin"

            if (newNumberOfItems == 0 && hasSearchButtonBeenPressed && viewModel.wasBinFound.value!!)
                AlerterUtils.startWarningAlerter(requireActivity(), "Bin is empty")

            hasSearchButtonBeenPressed = false
        }


        viewModel.wasBinFound.observe(viewLifecycleOwner) {hasBinBeenFound ->
            if (!hasBinBeenFound && hasSearchButtonBeenPressed){
                hasSearchButtonBeenPressed = false
                progressDialog.dismiss()
                AlerterUtils.startErrorAlerter(requireActivity(), getString(R.string.bin_not_found))
            }
        }

        viewModel.listOfProducts.observe(viewLifecycleOwner) { newProductInfo ->
            newProductInfo?.let {
                progressDialog.dismiss()
                adapter.data = it
            }
        }
    }




    // Handle item click events in the RecyclerView
    override fun onItemClickListener(view: View, position: Int) {
        viewModel.setChosenAdapterPosition(position)
        view.findNavController().navigate(R.id.action_productToBinFragment_to_productDetailsFragment)
    }


}