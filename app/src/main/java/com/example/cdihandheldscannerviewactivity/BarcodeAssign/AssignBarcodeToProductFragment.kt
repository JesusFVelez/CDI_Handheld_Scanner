package com.example.cdihandheldscannerviewactivity.BarcodeAssign

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.example.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.example.cdihandheldscannerviewactivity.databinding.FragmentAssignBarcodeToProductBinding

class AssignBarcodeToProductFragment: Fragment() {

    private lateinit var itemNumberEditText: EditText
    private lateinit var itemBarcodeEditText: EditText
    private lateinit var itemNumberTextView: TextView
    private lateinit var itemDescriptionTextView: TextView
    private lateinit var itemBarcodeTextView: TextView
    private lateinit var searchProductButton: Button
    private lateinit var addButton: Button
    private lateinit var binding: FragmentAssignBarcodeToProductBinding

    private val viewModel: AssignBarcodeViewModel by activityViewModels()

    private var hasPageJustStarted: Boolean = false
    private var wasSearchStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_assign_barcode_to_product, container, false)

        initUIElements()
        initObservers()

        return binding.root
    }

    // Handle onResume lifecycle event
    override fun onResume() {
        super.onResume()
        val bundle = arguments
        val lastFragmentName : String = BundleUtils.getPastFragmentNameFromBundle(bundle)
        if(lastFragmentName == "HomeScreen"){
            clearFragmentState()
            bundle?.clear()
        }
        hasPageJustStarted = false
        itemNumberEditText.text.clear()
    }

    // Handle onPause lifecycle event
    override fun onPause() {
        super.onPause()

        wasSearchStarted = false
    }

    override fun onStop() {
        super.onStop()
    }

    private fun initUIElements() {
        itemNumberEditText = binding.itemNumberEditText
        itemBarcodeEditText = binding.itemBarcodeEditText

        itemNumberTextView = binding.itemNumber
        itemDescriptionTextView = binding.itemDescription
        itemBarcodeTextView = binding.currentBarcodeText

        searchProductButton = binding.searchProductButton
        searchProductButton.setOnClickListener{
            validateItem()
        }

        addButton = binding.addButton
        addButton.setOnClickListener{
            validateBarcode()
        }

        itemNumberEditText.requestFocus()
    }

    private fun initObservers(){
        viewModel.wasItemFound.observe(viewLifecycleOwner) { wasItemConfirmed ->
            if(wasItemConfirmed && !hasPageJustStarted && itemNumberEditText.text.toString() != "") {
                viewModel.getItems(itemNumberEditText.text.toString())
                itemNumberEditText.isEnabled = false
            } else if (itemNumberEditText.text.toString() != ""){
                AlerterUtils.startErrorAlerter(
                    requireActivity(),
                    viewModel.errorMessage.value!!["wasItemFoundError"]!!
                )
            }
        }

        viewModel.itemInfo.observe(viewLifecycleOwner) { itemInfo ->
            if(itemInfo != null) {

                binding.middleDiv.visibility = View.VISIBLE
                itemBarcodeEditText.isEnabled = true

                itemNumberTextView.text = viewModel.itemInfo.value!!.itemNumber
                itemDescriptionTextView.text = viewModel.itemInfo.value!!.itemDescription
                itemBarcodeTextView.text = viewModel.itemInfo.value!!.itemBarcode
                
                itemBarcodeEditText.requestFocus()
            }
        }

        viewModel.isBarcodeValid.observe(viewLifecycleOwner) { wasBarcodeValid ->
            if(wasBarcodeValid && itemBarcodeEditText.text.toString() != "") {
                viewModel.setBarcode(itemNumberEditText.text.toString(), itemBarcodeEditText.text.toString())
                binding.middleDiv.visibility = View.GONE
                itemNumberEditText.isEnabled = true
                itemBarcodeEditText.isEnabled = false
                itemNumberEditText.requestFocus()

                AlerterUtils.startSuccessAlert(requireActivity(), "Barcode Set", "Successfully changed barcode to ${itemBarcodeEditText.text.toString()}")
                itemBarcodeEditText.text.clear()

            } else if (itemBarcodeEditText.text.toString() != "") {
                AlerterUtils.startErrorAlerter(
                    requireActivity(),
                    viewModel.errorMessage.value!!["isBarcodeValidError"]!!
                )
            }
        }
    }

    private fun validateItem(){
        viewModel.validateItem(itemNumberEditText.text.toString())
    }

    private fun validateBarcode(){
        viewModel.validateBarcode(itemBarcodeEditText.text.toString())
    }

    private fun clearFragmentState() {
        itemNumberEditText.isEnabled = true
        //itemNumberEditText.text.clear()

        itemBarcodeEditText.isEnabled = false
        //itemBarcodeEditText.text.clear()

        binding.middleDiv.visibility = View.GONE

    }

}
