package com.scannerapp.cdihandheldscannerviewactivity.ReceivingProductsIntoWarehouse

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.content.Context
import android.view.Gravity
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
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.DoorBin
import com.scannerapp.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.scannerapp.cdihandheldscannerviewactivity.databinding.ReceivingMainFragmentBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemsInBinList

class ReceivingProductsMainFragment : Fragment(){

    // Main Fragment Variables
    private lateinit var binNumberAutoCompleteTextView: AutoCompleteTextView
    private lateinit var purchaseOrderNumberTextView: TextView
    private lateinit var searchButton: Button
    private lateinit var finishButton: Button
    private lateinit var addButton: FloatingActionButton

    private lateinit var recyclerViewAdapter: itemsInDoorBinAdapter

    private lateinit var progressDialog: Dialog

    private val viewModel: ReceivingProductsViewModel by activityViewModels()
    private lateinit var binding: ReceivingMainFragmentBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.receiving_main_fragment, container, false)

        // Retrieve company ID from shared preferences
        val companyID:String = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        viewModel.setCompanyIDFromSharedPref(companyID)

        // Retrieve warehouse number from shared preferences
        val warehouseNumber: Int = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
        viewModel.setWarehouseNumberFromSharedPref(warehouseNumber)

        // Gets the username from the Shared Preferences
        val pickerUserName: String = SharedPreferencesUtils.getUserNameFromSharedPref(requireContext())
        viewModel.setPickerUserName(pickerUserName)

        initUIElements()
        initObservers()

        val bundle = arguments
        val lastFragmentName : String = BundleUtils.getPastFragmentNameFromBundle(bundle)

        progressDialog.show()
        if(lastFragmentName != "null")
            viewModel.getDoorBins()


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val bundle = arguments
        val lastFragmentName : String = BundleUtils.getPastFragmentNameFromBundle(bundle)
        if(lastFragmentName == "HomeScreen"){
            viewModel.clearListOfItems()
            viewModel.clearDoorBinText()
            finishButton.isEnabled = false
            addButton.isEnabled = false
            purchaseOrderNumberTextView.text = ""
            bundle?.clear()
        }
        // For whenever the fragment comes back from the item details fragment screen
        if(lastFragmentName == "null"){
            progressDialog.show()
            viewModel.getItemsInDoor()
            searchButton.isEnabled = false
            binNumberAutoCompleteTextView.isEnabled = false
        }
    }

    // Handles onPause lifecycle event
    override fun onPause(){
        super.onPause()
        viewModel.resetHasAPIBeenCalled()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun initUIElements(){
        binNumberAutoCompleteTextView = binding.DoorBinAutoTextView
        purchaseOrderNumberTextView = binding.PONumberTextView
        searchButton = binding.SearchBinButton
        searchButton.isEnabled = false
        finishButton = binding.FinishReceivingButton
        addButton = binding.addItemButton

        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        searchButton.setOnClickListener {
            if(binNumberAutoCompleteTextView.text.isNotEmpty()) {
                progressDialog.show()
                viewModel.getPreReceivingInfo()
                searchButton.isEnabled = false
                binNumberAutoCompleteTextView.isEnabled = false
                binNumberAutoCompleteTextView.setAdapter(null)
            }else
                AlerterUtils.startErrorAlerter(requireActivity(), "Please choose a door bin number from the drop down.")
        }

        addButton.setOnClickListener {
            val listener = object : PopupWindowUtils.Companion.PopupInputListener{
                override fun onConfirm(input: EditText) {
                    viewModel.getItemInfo(input.text.toString(), isEditing = false)
                    progressDialog.show()
                }
            }
            PopupWindowUtils.showConfirmationPopup(requireContext(),it.rootView,"Confirm Item Number", "Item Number", listener)
        }

        finishButton.setOnClickListener {
            val listener = object : PopupWindowUtils.Companion.PopupInputListener{
                override fun onConfirm(input: EditText) {
                    progressDialog.show()
                    viewModel.validateDestinationBin(input.text.toString())
                }
            }
            val popup = PopupWindowUtils.showConfirmationPopup(context = requireContext(),
                                                                anchor = it.rootView,
                                                                confirmationText = "Please enter the destination bin for all items received.",
                                                                confirmEditTextHint = "Destination Bin",
                                                                listener = listener)
        }
        viewModel.clearListOfItems()
        initRecyclerViewAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearAllItemsFromRecyclerView()
    }

    private fun verifyIfAllTheItemsHaveBeenReceived(listOfItems: List<ItemsInBinList>): Boolean{
        for(item in listOfItems){
            if(!item.wasItemAlreadyReceived)
                return false
        }
        return true
    }

    private fun initRecyclerViewAdapter(){
        val listener = object : itemInDoorBinClickListener {
            override fun onItemClickListener(view: View, position: Int) {
                val item = recyclerViewAdapter.data[position]
                if(!item.wasItemAlreadyReceived) {
                    viewModel.willEditCurrentValues()
                    viewModel.setCurrentlyChosenItemAdapterPosition(position)
                    viewModel.getItemInfo(recyclerViewAdapter.data[position].itemNumber, isEditing = true)
                    progressDialog.show()
                }
                else
                    AlerterUtils.startErrorAlerter(requireActivity(), "You have already moved this item and cannot move it again.")
            }
        }
        recyclerViewAdapter = itemsInDoorBinAdapter(listener, { hasItems ->
            val noItemsHaveInvalidLineUp = !verifyIfThereIsAtLeastOneItemWithInvalidLineUp(viewModel.listOfItemsToMoveInPreReceiving.value!!)
            val allItemsHaveBeenReceived = verifyIfAllTheItemsHaveBeenReceived(viewModel.listOfItemsToMoveInPreReceiving.value!!)
            finishButton.isEnabled = hasItems && noItemsHaveInvalidLineUp && !allItemsHaveBeenReceived
        },{ item: ItemsInBinList ->
            progressDialog.show()
            viewModel.deleteItemFromDoorBin(item.rowID)
        })
        val doesAtLeastOneItemHaveInvalidLineUp = verifyIfThereIsAtLeastOneItemWithInvalidLineUp(viewModel.listOfItemsToMoveInPreReceiving.value!!)
        if(doesAtLeastOneItemHaveInvalidLineUp)
            AlerterUtils.startErrorAlerter(requireActivity(), "At least one item in this order cannot be processed because it is either not physically present in this warehouse (Line Up W), has been discontinued (Line Up D), or it can not be received (Line Up S).")

        recyclerViewAdapter.addItems(viewModel.listOfItemsToMoveInPreReceiving.value!!)
        binding.totalPickingItemsList.adapter = recyclerViewAdapter
    }

    private fun verifyIfThereIsAtLeastOneItemWithInvalidLineUp(listOfItems: List<ItemsInBinList>): Boolean{
        for(item in listOfItems){
            if(item.doesItemHaveInvalidLineUp)
                return true
        }
        return false
    }

    private fun initBinNumberAutoCompleteTextView(newDoorBinsThatHavePreReceiving: List<DoorBin>){
        val arrayAdapterForAutoCompleteTextView =
            CustomDoorBinSuggestionAdapter(
                requireContext(),
                newDoorBinsThatHavePreReceiving
            )
        binNumberAutoCompleteTextView.setAdapter(arrayAdapterForAutoCompleteTextView)
        binNumberAutoCompleteTextView.width = ViewGroup.LayoutParams.MATCH_PARENT
        binNumberAutoCompleteTextView.setOnItemClickListener{parent, view, position, id ->
            val selectedItem = binNumberAutoCompleteTextView.adapter.getItem(position) as DoorBin
            viewModel.setCurrentlyChosenDoorBin(selectedItem)
            if(selectedItem.bin_receiving.uppercase() == "NOT ASSIGNED")
                searchButton.isEnabled = false
            else
                searchButton.isEnabled = true
        }
    }

    private fun clearAllItemsFromRecyclerView(){
        recyclerViewAdapter.clearAllItems()
        recyclerViewAdapter.notifyDataSetChanged()
    }

    private fun initObservers(){

        viewModel.allItemsMoved.observe(viewLifecycleOwner){wereAllItemsMoved ->
            progressDialog.dismiss()
            if(wereAllItemsMoved && viewModel.wasLastAPICallSuccessful.value!!) {
                AlerterUtils.startSuccessAlert(
                    requireActivity(),
                    "Success",
                    "Successfully Moved items to bin ${viewModel.destinationBin.value!!}"
                )
                viewModel.resetAllItemsMovedFlag()
                clearAllItemsFromRecyclerView()
                purchaseOrderNumberTextView.text = ""
                searchButton.isEnabled = true
                binNumberAutoCompleteTextView.isEnabled = true
                finishButton.isEnabled = false
            }
        }

        viewModel.isDoorBinEmpty.observe(viewLifecycleOwner){isDoorBinEmpty ->
            progressDialog.dismiss()
            if(isDoorBinEmpty && viewModel.hasAPIBeenCalled.value!!){
                AlerterUtils.startWarningAlerter(requireActivity(), "Selected door bin does not have any items")
                viewModel.resetHasAPIBeenCalled()
                addButton.isEnabled = true
            }else if(viewModel.hasAPIBeenCalled.value!!){
                addButton.isEnabled = true
                initRecyclerViewAdapter()
                viewModel.resetHasAPIBeenCalled()
            }


        }

        viewModel.doorBins.observe(viewLifecycleOwner){doorBinsList ->
            progressDialog.dismiss()
            if(doorBinsList.isNotEmpty()) {
                initBinNumberAutoCompleteTextView(doorBinsList)
                viewModel.resetHasAPIBeenCalled()
            }

        }

        viewModel.isValidDestinationBin.observe(viewLifecycleOwner){ isDestinationValid ->
            if(viewModel.hasAPIBeenCalled.value!! && isDestinationValid) {
                viewModel.moveItemsToFloorBin(viewModel.destinationBin.value!!)
                viewModel.resetHasAPIBeenCalled()
            }
            else if(viewModel.hasAPIBeenCalled.value!!) {
                progressDialog.dismiss()
                AlerterUtils.startErrorAlerter(
                    requireActivity(),
                    viewModel.errorMessage.value!!["validateDestinationBin"]!!
                )
                viewModel.resetHasAPIBeenCalled()
            }

        }

        viewModel.wasItemDeleted.observe(viewLifecycleOwner){wasItemDeleted ->
            progressDialog.dismiss()
            if(wasItemDeleted && viewModel.hasAPIBeenCalled.value!!){
                progressDialog.dismiss()
                AlerterUtils.startSuccessAlert(requireActivity(), "Success", "Successfully Deleted Item")
                viewModel.resetHasAPIBeenCalled()
            }else if(viewModel.hasAPIBeenCalled.value!!){
                progressDialog.dismiss()
                AlerterUtils.startErrorAlerter(requireActivity(), viewModel.errorMessage.value!!["wasItemDeleted"]!!)
                viewModel.resetHasAPIBeenCalled()
            }
        }

        viewModel.wasItemFound.observe(viewLifecycleOwner){wasItemFound ->
            progressDialog.dismiss()
            if(!wasItemFound && viewModel.hasAPIBeenCalled.value!!){
                AlerterUtils.startErrorAlerter(requireActivity(),viewModel.errorMessage.value!!["wasItemFoundError"]!!)
                viewModel.resetHasAPIBeenCalled()
            }else if(viewModel.hasAPIBeenCalled.value!!){
                val bundle = BundleUtils.getBundleToSendFragmentNameToNextFragment("ReceivingProductsMainFragment")
                findNavController().navigate(R.id.action_receivingProductsMainFragment_to_receivingProductsDetailsFragment, bundle)
                viewModel.resetHasAPIBeenCalled()
            }

        }

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner){ wasLastAPICallSuccessful ->
            if(!wasLastAPICallSuccessful && viewModel.hasAPIBeenCalled.value!!){
                viewModel.resetHasAPIBeenCalled()
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity(), viewModel.networkErrorMessage.value!!)
            }
        }

        viewModel.wasPreReceivingFound.observe(viewLifecycleOwner){wasPrereFound ->
            if(!wasPrereFound && viewModel.hasAPIBeenCalled.value!!) {
                progressDialog.dismiss()
                viewModel.resetHasAPIBeenCalled()
                AlerterUtils.startErrorAlerter(
                    requireActivity(),
                    viewModel.errorMessage.value!!["wasPreReceivingFoundError"]!!
                )
            }
        }

        viewModel.preReceivingInfo.observe(viewLifecycleOwner){newPreReceivingInfo ->
            if(newPreReceivingInfo != null && viewModel.hasAPIBeenCalled.value!!) {
                viewModel.resetHasAPIBeenCalled()
                purchaseOrderNumberTextView.text = newPreReceivingInfo.tt_purchase_order.uppercase()

                viewModel.getItemsInDoor()
            }
        }
    }



    class CustomDoorBinSuggestionAdapter(context: Context, private var suggestions: List<DoorBin>): ArrayAdapter<DoorBin>(context, 0, suggestions){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.receiving_door_bins_suggestion_view, parent, false)
            val doorBinTextView = view.findViewById<TextView>(R.id.doorBinValueTextView)
            val preReceivingTextView = view.findViewById<TextView>(R.id.preReceivingValueTextView)


            val item = suggestions[position]
            doorBinTextView.text = item.bin_number.uppercase()
            preReceivingTextView.text = item.bin_receiving.uppercase()

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
                return (resultValue as DoorBin).bin_number + " - " + resultValue.bin_receiving
            }
        }
        override fun getFilter(): Filter {
            return filter

        }

    }


}