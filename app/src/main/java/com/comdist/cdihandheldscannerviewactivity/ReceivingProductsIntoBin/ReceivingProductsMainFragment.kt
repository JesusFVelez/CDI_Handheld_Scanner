package com.comdist.cdihandheldscannerviewactivity.ReceivingProductsIntoBin

import android.os.Bundle
import android.widget.Button
import android.content.Context
import android.widget.Filter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import android.view.View
import android.view.ViewGroup
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DoorBin
import com.comdist.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.comdist.cdihandheldscannerviewactivity.databinding.FragmentReceivingItemsMainBinding
import com.comdist.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils

class ReceivingProductsMainFragment : Fragment() {


    // Main Fragment Variables
    private lateinit var binNumberAutoCompleteTextView: AutoCompleteTextView
    private lateinit var doorBinNumberTextView: TextView
    private lateinit var preReceivingTextView: TextView
    private lateinit var purchaseOrderNumberTextView: TextView
    private lateinit var searchButton: Button
    private lateinit var finishButton: Button
    private lateinit var addButton: Button

    private val viewModel: ReceivingProductsViewModel by activityViewModels()
    private lateinit var binding: FragmentReceivingItemsMainBinding

    private var hasPageJustStarted: Boolean = false
    private var wasSearchStarted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_receiving_items_main, container, false)

        // Retrieve company ID from shared preferences
        val companyID:String = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        viewModel.setCompanyIDFromSharedPref(companyID)

        // Retrieve warehouse number from shared preferences
        val warehouseNumber: Int = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
        viewModel.setWarehouseNumberFromSharedPref(warehouseNumber)

        hasPageJustStarted = true

        initUIElements()
        initObservers()

        viewModel.getDoorBins()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val bundle = arguments
        val lastFragmentName : String = BundleUtils.getPastFragmentNameFromBundle(bundle)
        if(lastFragmentName == "HomeScreen"){
            clearFragmentState()
            bundle?.clear()
        }

        hasPageJustStarted = false
    }

    // Handles onPause lifecycle event
    override fun onPause(){
        super.onPause()

        wasSearchStarted = false
    }

    override fun onStop() {
        super.onStop()
    }


    private fun initUIElements(){
        binNumberAutoCompleteTextView = binding.DoorBinAutoTextView
        doorBinNumberTextView = binding.DoorBinTextView
        preReceivingTextView = binding.PreReceivingTextView
        purchaseOrderNumberTextView = binding.POInfoTextView
        searchButton = binding.SearchBinButton
        finishButton = binding.FinishReceivingButton
        addButton = binding.addItemButton

        searchButton.setOnClickListener {
            viewModel.getPreReceivingInfo(binNumberAutoCompleteTextView.text.toString())
        }

        finishButton.setOnClickListener {

        }

    }

    private fun initBinNumberAutoCompleteTextView(binNumbers: List<DoorBin>){
        val arrayAdapterForAutoCompleteTextView = CustomBinNumberSuggestionAdapter(requireContext(),binNumbers)
    }
    private fun initObservers(){



        viewModel.doorBins.observe(viewLifecycleOwner){doorBinsList ->
            if(doorBinsList.isNotEmpty())
                TODO("Add things here")
        }
        viewModel.preReceivingInfo.observe(viewLifecycleOwner){newPreReceivingInfo ->
            if(newPreReceivingInfo.isNotEmpty()){

            }

        }
    }

    private fun clearFragmentState() {
        binding.middleDiv.visibility = View.GONE
    }

    class CustomBinNumberSuggestionAdapter(context: Context, private var suggestions: List<DoorBin>): ArrayAdapter<DoorBin>(context, 0, suggestions){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.suggestion_receiving_door_bin_view, parent, false)
            val binNumberAutoCompleteTextView = view.findViewById<TextView>(R.id.preReceivingTextView)

            val item = suggestions[position]
            binNumberAutoCompleteTextView.text = item.bin_number

            return view
        }

        private val originalList = ArrayList(suggestions)

        private val filter = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val query = constraint?.toString()?.lowercase()
                val filteredList = if (query.isNullOrEmpty()) {
                    originalList
                } else {
                    originalList.filter {
                        it.bin_number.lowercase().contains(query)
                    }
                }

                results.values = filteredList
                results.count = filteredList.size

                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clear()
                if (results?.count ?: 0 > 0) {
                    addAll(results?.values as List<DoorBin>)
                    notifyDataSetChanged()
                } else {
                    notifyDataSetChanged()
                }
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as DoorBin).bin_number
            }
        }
    }


//    val binNumberTextView = view.findViewById<TextView>(R.id.binNumberText)
//    val productDescription = view.findViewById<TextView>(R.id.productNameText)
//    val itemNumber = view.findViewById<TextView>(R.id.itemNumberText)
//    val lotNumber = view.findViewById<TextView>(R.id.lotNumberText)
//    val quantity = view.findViewById<TextView>(R.id.onHandQtyText)
//    val expirationDate = view.findViewById<TextView>(R.id.expDateInfoText)

}