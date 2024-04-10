package com.comdist.cdihandheldscannerviewactivity.ReceivingProductsIntoBin

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.comdist.cdihandheldscannerviewactivity.databinding.FragmentReceivingItemsMainBinding

class ReceivingProductsMainFragment : Fragment() {


    // Main Fragment Variables
    private lateinit var binNumberAutoCompleteTextView: AutoCompleteTextView
    private lateinit var doorBinNumberTextView: TextView
    private lateinit var preReceivingTextView: TextView
    private lateinit var purchaseOrderNumberTextView: TextView
    private lateinit var searchButton: Button

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

        initUIElements()
        initObservers()

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
        binNumberAutoCompleteTextView.text.clear()
        doorBinNumberTextView.text = ""
        preReceivingTextView.text = ""
        purchaseOrderNumberTextView.text = ""
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

        binNumberAutoCompleteTextView.setOnEditorActionListener{ v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                getPreReceiving()
                true
            } else {
                false
            }
        }

    }

    private fun initObservers(){

    }

    private fun clearFragmentState() {
        binding.middleDiv.visibility = View.GONE
    }

}