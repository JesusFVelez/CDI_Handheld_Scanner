package com.example.cdihandheldscannerviewactivity.ProductsInBin

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.databinding.FragmentProductInBinBinding
import com.example.cdihandheldscannerviewactivity.Network.WarehouseInfo

class ProductInBinFragment : Fragment(), ProductsInBinItemOnClickListener{

    private lateinit var binding: FragmentProductInBinBinding
    private lateinit var warehouseSpinner: Spinner
    private lateinit var binNumberEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var numberOfItemsTextView: TextView
    private val viewModel: ProductsInBinViewModel by activityViewModels()
    private lateinit var adapter : ProductsInBinAdapter
    private lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //TODO place all variables that are binded to UI elements into an init UI elements function and call it in the onCreateView
    // TODO (3) Anñadir funcionalidad que haga que el formato del editText del Bin Number siempre tenga los guiones entremedio (ver commentarios en Figma)
    // TODO Añadir la habilidad de manage network errors a esta parte del app
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
        progressDialog = Dialog(requireContext()).apply{
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
            show()
        }
        initObservers()
        binding.productsInBinList.layoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        // TODO add a database to store information like the company code and other things
        searchButton.setOnClickListener{
                // here goes the API Call for the filling of the recycler view
                progressDialog.show()
                viewModel.getProductInfoFromBackend(warehouseSpinner.selectedItem.toString(),binNumberEditText.text.toString())
        }
        return binding.root
    }

    private fun initUIElements(){
        warehouseSpinner = binding.warehouseSpinner
        binNumberEditText = binding.binNumberEditText
        searchButton = binding.searchBinButton
        numberOfItemsTextView = binding.totalProductsTextView
    }
    private fun initObservers(){

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner){wasAPICallSuccesfull ->
            if(!wasAPICallSuccesfull){
                progressDialog.dismiss()
                Toast.makeText(requireContext(),"Something went wrong with the request!", Toast.LENGTH_SHORT).show()
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