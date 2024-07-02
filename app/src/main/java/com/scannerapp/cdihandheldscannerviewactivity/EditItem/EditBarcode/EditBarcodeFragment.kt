package com.scannerapp.cdihandheldscannerviewactivity.EditItem.EditBarcode

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemData
import com.scannerapp.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.PopupWindowUtils.Companion.PopupInputListener
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.scannerapp.cdihandheldscannerviewactivity.databinding.EditItemEditBarcodeFragmentBinding
import com.tapadoo.alerter.Alerter

class EditBarcodeFragment : Fragment() {

    private lateinit var binding: EditItemEditBarcodeFragmentBinding
    private lateinit var itemNumberTextView: TextView
    private lateinit var itemNameTextView: TextView
    private lateinit var additionalBarcodeRecyclerView: RecyclerView
    private lateinit var mainBarcodeTextView: TextView
    private lateinit var addBarcodeButton: FloatingActionButton


    private lateinit var viewModel: EditBarcodeViewModel

    private lateinit var adapter: EditItemBarcodesAdapter

    private lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.edit_item_edit_barcode_fragment, container, false)


        val args: EditBarcodeFragmentArgs by navArgs()
        val itemInfo = args.itemInfo
        initViewModel(itemInfo)
        initUIElements()
        initObservers()
        viewModel.getBarcodesFromBackend()
        progressDialog.show()

        return binding.root
    }


    private fun initObservers() {
        viewModel.mainBarcode.observe(viewLifecycleOwner) { mainBarcode ->
            if (mainBarcode!!.isNotEmpty())
                mainBarcodeTextView.text = mainBarcode
            else
                mainBarcodeTextView.text = "No Barcode Found"
            progressDialog.dismiss()
        }

        viewModel.listOfBarcodesOfItem.observe(viewLifecycleOwner) { listOfBarcodes ->
            if (!listOfBarcodes.isNullOrEmpty() && listOfBarcodes[0] != null)
                adapter.data = listOfBarcodes.toMutableList()
            else
                adapter.data = mutableListOf()
            progressDialog.dismiss()
        }

        viewModel.couldAddOrRemoveBarcode.observe(viewLifecycleOwner) { couldAddOrRemoveBarcode ->
            if (couldAddOrRemoveBarcode) {
                viewModel.getBarcodesFromBackend()
                AlerterUtils.startSuccessAlert(requireActivity(), "Success", "Barcode has been successfully modified.")
            }
            else {
                progressDialog.dismiss()
                AlerterUtils.startErrorAlerter(requireActivity(), viewModel.errorMessage.value!!)
            }
        }



    }

    private fun populateUIWithItemInfo(){
        itemNumberTextView.text = viewModel.itemInfo.value?.itemNumber
        itemNameTextView.text = viewModel.itemInfo.value?.itemDescription
    }


    private fun initViewModel(itemInfo: ItemData?){

        // Create the ViewModelFactory with optional parameters
        val factory = EditBarcodeViewModelFactory(itemInfo)

        // Setting the view model
        viewModel = ViewModelProvider(this, factory)[EditBarcodeViewModel::class.java]

        val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        viewModel.setCompanyIDFromSharedPref(companyID)


    }
    


    private fun initUIElements(){
        itemNumberTextView = binding.itemNumberTextView
        itemNameTextView = binding.itemNameTextView

        addBarcodeButton = binding.addBarcodeButton
        addBarcodeButton.setOnClickListener {
            startBarcodeAddOrUpdatePopup()
        }

        additionalBarcodeRecyclerView = binding.additionalBarcodeRecyclerView
        val listener =
            BarcodeClickListener { view, position ->
                startBarcodeAddOrUpdatePopup(true, adapter.data[position])
            }
        adapter = EditItemBarcodesAdapter(listener){ barcodeToRemove ->
            progressDialog.show()
            viewModel.removeBarcodeFromBackend(barcodeToRemove, false)
        }
        additionalBarcodeRecyclerView.adapter = adapter

        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        initMainBarcodeComponents()
        populateUIWithItemInfo()
    }

    private fun initMainBarcodeComponents(){
        mainBarcodeTextView = binding.mainBarcodeLayout.findViewById(R.id.barcodeTextView)
        binding.mainBarcodeLayout.setOnClickListener {
            startBarcodeAddOrUpdatePopup(isMainBarcode = true, isUpdatingBarcode = true, barcodeToUpdate = mainBarcodeTextView.text.toString())
        }
        binding.mainBarcodeLayout.findViewById<ImageButton>(R.id.removeBarcodeIcon).setOnClickListener{
            // Create popup window to ask whether user wants to delete barcode or not
            val popupWindow = PopupWindowUtils.createQuestionPopup(
                it.context,
                "Are you sure you want to delete this barcode for this item?",
                "Delete Barcode"
            )
            popupWindow.contentView.findViewById<Button>(R.id.YesButton)
                .setOnClickListener {
                    popupWindow.dismiss()
                    progressDialog.show()
                    viewModel.removeBarcodeFromBackend(mainBarcodeTextView.text.toString(), true)
                }
            popupWindow.contentView.findViewById<Button>(R.id.NoButton)
                .setOnClickListener {
                    popupWindow.dismiss()
                }
            popupWindow.showAtLocation(it.rootView, Gravity.CENTER, 0, 0)
        }
    }

    private fun startBarcodeAddOrUpdatePopup(isUpdatingBarcode: Boolean = false, barcodeToUpdate: String = "", isMainBarcode: Boolean = false) {
        val listener = object : PopupInputListener {
            override fun onConfirm(input: EditText) {
                if(isUpdatingBarcode)
                    viewModel.updateBarcodeToBackend(barcodeToUpdate, input.text.toString(), isMainBarcode)
                else
                    viewModel.addBarcodeToBackend(input.text.toString(), isMainBarcode)
                progressDialog.show()
            }
        }
        val confirmationText = if (isUpdatingBarcode) "Update Barcode" else "Add Barcode"
        val popupWindow = PopupWindowUtils.showConfirmationPopup(
            requireContext(),
            requireView(),
            confirmationText,
            "Scan Code",
            listener
        )

        if(isUpdatingBarcode)
            popupWindow.contentView.findViewById<EditText>(R.id.confirmationEditText).setText(barcodeToUpdate)

    }


}