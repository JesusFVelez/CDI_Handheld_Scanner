package com.comdist.cdihandheldscannerviewactivity.ReceivingProductsIntoBin

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.comdist.cdihandheldscannerviewactivity.databinding.FragmentReceivingItemsDetailsBinding

class RecevingProductsDetailsFragment: Fragment() {


        // Details Fragment Variables
        private lateinit var itemNumberTextView: TextView
        private lateinit var lotTextView: TextView
        private lateinit var binLocationTextView: TextView
        private lateinit var expirationDateTextView: TextView
        private lateinit var itemNameTextView: TextView
        private lateinit var newBinEditText: EditText
        private lateinit var newLotAutoCompleteTextView: AutoCompleteTextView
        private lateinit var quantityEditText: EditText
        private lateinit var newExpirationDateEditText: EditText

        private val viewModel: ReceivingProductsViewModel by activityViewModels()
        private lateinit var binding: FragmentReceivingItemsDetailsBinding

        private var hasPageJustStarted: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedIntanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_receiving_items_details, container, false)

        initUIElements()
        initObservers()

        return binding.root
    }

    override fun onResume(){
        super.onResume()
        val bundle = arguments
        val lastFragmentName : String = BundleUtils.getPastFragmentNameFromBundle(bundle)
        if(lastFragmentName == "HomeScreen"){
            clearFragmentState()
            bundle?.clear()
        }
        hasPageJustStarted = false
    }

    override fun onPause(){
        super.onPause()
    }

    override fun onStop(){
        super.onStop()
    }

    private fun initUIElements(){
        itemNumberTextView = binding.itemNumberTextView
        lotTextView = binding.lotTextView
        binLocationTextView = binding.binLocationTextView
        expirationDateTextView = binding.expirationDateTextView
        itemNameTextView = binding.itemNameTextView
        newBinEditText = binding.NewBinEditText
        newLotAutoCompleteTextView = binding.newLotAutoCompleteTextView
        quantityEditText = binding.QuantityEditText
        newExpirationDateEditText = binding.NewExpirationDateEditText
    }

    private fun initObservers(){

    }

    private fun clearFragmentState() {
        binding.upperDiv.visibility = View.GONE
    }
}