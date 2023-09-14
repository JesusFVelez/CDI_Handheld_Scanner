package com.example.cdihandheldscannerviewactivity.ProductsInBin

import android.annotation.SuppressLint
import android.app.Dialog
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdihandheldscannerviewactivity.Network.WarehouseInfo
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.databinding.FragmentProductInBinBinding

class ProductInBinFragment : Fragment(), ProductsInBinItemOnClickListener{

    private lateinit var binding: FragmentProductInBinBinding
    private lateinit var warehouseSpinner: Spinner
    private lateinit var binNumberEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var numberOfItemsTextView: TextView
    private val viewModel: ProductsInBinViewModel by activityViewModels()
    private lateinit var adapter : ProductsInBinAdapter
    private lateinit var progressDialog: Dialog

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback : ConnectivityManager.NetworkCallback
    private lateinit var networkRequest: NetworkRequest

    private var hasPageJustStarted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // TODO - place all variables that are binded to UI elements into an init UI elements function and call it in the onCreateView
    // TODO - Anñadir funcionalidad que haga que el formato del editText del Bin Number siempre tenga los guiones entremedio (ver commentarios en Figma)
    // TODO (Ongoing) -  Añadir la habilidad de manage network errors a esta parte del app
    // TODO - add a database to store information like the company code and other things
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_in_bin, container, false)
//        viewModel = ViewModelProvider(this)[ProductsInBinViewModel::class.java]
        // Using the LiveData with the UI is not working so I commented it
//        binding.productsInBinViewModel = viewModel
//        binding.lifecycleOwner = this
        initUIElements()
        initSpinner()
        adapter = ProductsInBinAdapter(this)
        binding.productsInBinList.adapter = adapter
        initObservers()
        binding.productsInBinList.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        connectivityManager = requireContext().getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkRequest = NetworkRequest.Builder().build()
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Handle connection
                if (hasPageJustStarted)
                    Toast.makeText(requireContext(), resources.getString(R.string.internet_restored), Toast.LENGTH_SHORT).show()
                else
                    hasPageJustStarted = true

                if(warehouseSpinner.selectedItem == null) {
                    activity?.runOnUiThread {
                        progressDialog.show()
                        viewModel.getWarehousesFromBackendForSpinner()
                    }
                }
            }

            override fun onLost(network: Network) {
                // Handle disconnection
                hasPageJustStarted = true
                Toast.makeText(requireContext(), resources.getString(R.string.internet_lost), Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun initUIElements(){
        warehouseSpinner = binding.warehouseSpinner
        binNumberEditText = binding.binNumberEditText
        searchButton = binding.searchBinButton
        searchButton.setOnClickListener{
            // here goes the API Call for the filling of the recycler view
            progressDialog.show()
            viewModel.getProductInfoFromBackend(warehouseSpinner.selectedItem.toString(),binNumberEditText.text.toString())
        }
        numberOfItemsTextView = binding.totalProductsTextView
        progressDialog = Dialog(requireContext()).apply{
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
            show()
        }
    }

    override fun onResume() {
        super.onResume()
        hasPageJustStarted = false
        // Register the callback
        if (connectivityManager != null) {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        }
    }

    override fun onPause() {
        super.onPause()

        // Unregister the callback
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
    private fun initObservers(){

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner){wasAPICallSuccesfull ->
            if(!wasAPICallSuccesfull){
                progressDialog.dismiss()
                Toast.makeText(requireContext(),resources.getString(R.string.network_request_error_message), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.numberOfItemsInBin.observe(viewLifecycleOwner){newNumberOfItems ->
            if (newNumberOfItems > 1 || newNumberOfItems == 0)
                numberOfItemsTextView.text = "${newNumberOfItems.toString()} items in Bin"
            else
                numberOfItemsTextView.text = "${newNumberOfItems.toString()} item in Bin"
        }

        viewModel.listOfWarehouses.observe(viewLifecycleOwner) {newWarehousesList ->
            progressDialog.dismiss()
            fillSpinnerWithWarehouses(newWarehousesList)
        }

        viewModel.wasBinFound.observe(viewLifecycleOwner) {hasBinBeenFound ->
            if (!hasBinBeenFound){
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Selected Bin Has not been found!", Toast.LENGTH_LONG).show()
            }
        }

        viewModel.listOfProducts.observe(viewLifecycleOwner) { newProductInfo ->
            newProductInfo?.let {
                progressDialog.dismiss()
                adapter.data = it
            }

        }

    }
    private fun fillSpinnerWithWarehouses( newWarehouseList : List<WarehouseInfo>){
        val warehouses = mutableListOf<String>()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, warehouses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        warehouseSpinner.adapter = adapter
        for (aWarehouse in viewModel.listOfWarehouses.value!!) {
            warehouses.add(aWarehouse.warehouseName)
        }
        adapter.notifyDataSetChanged()
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun initSpinner(){

        val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            // Reset background when spinner closes
            warehouseSpinner.setBackgroundResource(R.drawable.drop_down_background)
            viewModel.setIsSpinnerArrowUp(false)
        }

        // set what happens whenever the Spinner is clicked
        warehouseSpinner.setOnTouchListener{view, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    warehouseSpinner.setBackgroundResource(R.drawable.drop_down_arrow_up)
                    warehouseSpinner.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
                    viewModel.setIsSpinnerArrowUp(true)
                }
            }
            false
        }

        // set what happens whenever an item is clicked in the spinner
        warehouseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                warehouseSpinner.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
                viewModel.setIsSpinnerArrowUp(false)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Reset the background when the user touches outside of the Spinner
        binding.root.setOnTouchListener { _, _ ->
            if (viewModel.isSpinnerArrowUp.value!!) {
                warehouseSpinner.setBackgroundResource(R.drawable.drop_down_background)
                warehouseSpinner.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
                viewModel.setIsSpinnerArrowUp(false)
            }
            false
        }
    }

    override fun onItemClickListener(view: View, position: Int) {
        viewModel.setChosenAdapterPosition(position)
        view.findNavController().navigate(R.id.action_productToBinFragment_to_productDetailsFragment)
    }


}