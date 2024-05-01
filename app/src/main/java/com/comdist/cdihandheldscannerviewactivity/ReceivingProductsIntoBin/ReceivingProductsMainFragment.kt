package com.comdist.cdihandheldscannerviewactivity.ReceivingProductsIntoBin

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
import androidx.navigation.fragment.findNavController
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.DoorBin
import com.comdist.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.comdist.cdihandheldscannerviewactivity.databinding.ReceivingMainFragmentBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ReceivingProductsMainFragment : Fragment(){

    // Main Fragment Variables
    private lateinit var binNumberAutoCompleteTextView: AutoCompleteTextView
    private lateinit var doorBinNumberTextView: TextView
    private lateinit var preReceivingTextView: TextView
    private lateinit var purchaseOrderNumberTextView: TextView
    private lateinit var searchButton: Button
    private lateinit var finishButton: Button
    private lateinit var addButton: FloatingActionButton

    private lateinit var recyclerViewAdapter: itemsInDoorBinAdapter

    private lateinit var progressDialog: Dialog

    private val viewModel: ReceivingProductsViewModel by activityViewModels()
    private lateinit var binding: ReceivingMainFragmentBinding

    private var wasSearchStarted: Boolean = false

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

        initUIElements()
        initObservers()

        progressDialog.show()
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
            clearMiddleDiv()
            bundle?.clear()
        }
        // For whenever the fragment comes back from the item details fragment screen
        if(lastFragmentName == "null"){
            progressDialog.show()
            viewModel.getPreReceivingInfo()
            searchButton.isEnabled = false
            binNumberAutoCompleteTextView.isEnabled = false
        }
    }

    // Handles onPause lifecycle event
    override fun onPause(){
        super.onPause()

        viewModel.resetHasAPIBeenCalled()
        wasSearchStarted = false
    }

    override fun onStop() {
        super.onStop()
    }

    private fun initUIElements(){
        binNumberAutoCompleteTextView = binding.DoorBinAutoTextView
        doorBinNumberTextView = binding.DoorBinTextView
        preReceivingTextView = binding.PreReceivingTextView
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
            }else
                AlerterUtils.startErrorAlerter(requireActivity(), "Please choose a door bin number from the drop down.")
        }

        addButton.setOnClickListener {
            val listener = object : PopupWindowUtils.Companion.PopupInputListener{
                override fun onConfirm(input: EditText) {
                    viewModel.getItemInfo(input.text.toString())
                    progressDialog.show()
                }
            }
            PopupWindowUtils.showConfirmationPopup(requireContext(),it.rootView,"Confirm Item Number", "Item Number", listener)
        }

        finishButton.setOnClickListener {
            val questionPopup = PopupWindowUtils.createQuestionPopup(requireContext(), "Are you sure you would like to finish moving items from door bin to floor bin (9970F)?", "Move Items?")
            questionPopup.contentView.findViewById<Button>(R.id.YesButton).setOnClickListener{
                progressDialog.show()
                questionPopup.dismiss()
                viewModel.moveItemsToFloorBin()
            }

            questionPopup.contentView.findViewById<Button>(R.id.NoButton).setOnClickListener{
                questionPopup.dismiss()
            }

            questionPopup.showAtLocation(requireView(), Gravity.CENTER, 0, 0)

        }
        viewModel.clearListOfItems()
        initRecyclerViewAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearAllItemsFromRecyclerView()
    }

    private fun initRecyclerViewAdapter(){
        val listener = object : itemInDoorBinClickListener {
            override fun onItemClickListener(view: View, position: Int) {
                viewModel.willEditCurrentValues()
                viewModel.setCurrentlyChosenItemAdapterPosition(position)
                viewModel.getItemInfo(recyclerViewAdapter.data[position].itemNumber)
                progressDialog.show()
            }
        }
        recyclerViewAdapter = itemsInDoorBinAdapter(listener, { hasItems ->
            finishButton.isEnabled = hasItems
        },{ item: itemsInDoorBinAdapter.ItemInDoorBinDataClass ->
            viewModel.deleteItemFromDoorBin(item.rowIDForDoorBin)
        })
        recyclerViewAdapter.addItems(viewModel.listOfItemsToMoveInPreReceiving.value!!)
        binding.totalPickingItemsList.adapter = recyclerViewAdapter
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
            if(wereAllItemsMoved) {
                AlerterUtils.startSuccessAlert(
                    requireActivity(),
                    "Success",
                    "Successfully Moved items to floor bin (9970F)"
                )
                viewModel.resetAllItemsMovedFlag()
                clearAllItemsFromRecyclerView()
                clearMiddleDiv()
                searchButton.isEnabled = true
                binNumberAutoCompleteTextView.isEnabled = true
                finishButton.isEnabled = false
            }
        }

        viewModel.isDoorBinEmpty.observe(viewLifecycleOwner){isDoorBinEmpty ->
            progressDialog.dismiss()
            if(isDoorBinEmpty && viewModel.hasAPIBeenCalled.value!!){
                AlerterUtils.startWarningAlerter(requireActivity(), "Selected door bin does not have any items")
            }else if(viewModel.hasAPIBeenCalled.value!!){
                initRecyclerViewAdapter()
            }
            viewModel.resetHasAPIBeenCalled()

        }

        viewModel.doorBins.observe(viewLifecycleOwner){doorBinsList ->
            progressDialog.dismiss()
            viewModel.resetHasAPIBeenCalled()
            if(doorBinsList.isNotEmpty())
                initBinNumberAutoCompleteTextView(doorBinsList)

        }

        viewModel.wasItemFound.observe(viewLifecycleOwner){wasItemFound ->
            progressDialog.dismiss()
            if(!wasItemFound && viewModel.hasAPIBeenCalled.value!!){
                AlerterUtils.startErrorAlerter(requireActivity(),viewModel.errorMessage.value!!["wasItemFoundError"]!!)
            }else if(viewModel.hasAPIBeenCalled.value!!){
                val bundle = BundleUtils.getBundleToSendFragmentNameToNextFragment("ReceivingProductsMainFragment")
                findNavController().navigate(R.id.action_receivingProductsMainFragment_to_receivingProductsDetailsFragment, bundle)
            }
            viewModel.resetHasAPIBeenCalled()
        }

        viewModel.wasLasAPICallSuccessful.observe(viewLifecycleOwner){wasLastAPICallSuccessful ->
            if(!wasLastAPICallSuccessful && viewModel.hasAPIBeenCalled.value!!){
                viewModel.resetHasAPIBeenCalled()
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity())
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
                viewModel.resetHasAPIBeenCalled()
                binding.middleDiv.visibility = View.VISIBLE
                doorBinNumberTextView.text =
                    binNumberAutoCompleteTextView.text.toString().uppercase()
                preReceivingTextView.text = newPreReceivingInfo.tt_pre_receiving_number.uppercase()
                purchaseOrderNumberTextView.text = newPreReceivingInfo.tt_purchase_order.uppercase()
                addButton.isEnabled = true
                viewModel.getItemsInDoor()
        }
    }

    private fun clearMiddleDiv() {
        binding.middleDiv.visibility = View.GONE
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
                return (resultValue as DoorBin).bin_number
            }
        }
        override fun getFilter(): Filter {
            return filter

        }

    }


}