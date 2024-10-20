package com.scannerapp.cdihandheldscannerviewactivity.ProductsInBin

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.scannerapp.cdihandheldscannerviewactivity.databinding.ProductsInBinFragmentBinding


//TODO - Figure out if there is a way to blur the background behind the Popup window so that it does not look as bad
class ProductInBinFragment : Fragment(), ProductsInBinItemOnClickListener{

    // Declare UI elements and ViewModel
    private lateinit var binding: ProductsInBinFragmentBinding
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
        binding = DataBindingUtil.inflate(inflater, R.layout.products_in_bin_fragment, container, false)
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
                hasSearchButtonBeenPressed = true
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
                AlerterUtils.startNetworkErrorAlert(requireActivity(), viewModel.networkErrorMessage.value!!)
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