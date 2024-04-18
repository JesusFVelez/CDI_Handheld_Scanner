package com.comdist.cdihandheldscannerviewactivity.MovingProductsBetweenBins

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.itemsInBin
import com.comdist.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.comdist.cdihandheldscannerviewactivity.databinding.FragmentBinMovementMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


class BinMovementFragment : Fragment() {

    private lateinit var binding: FragmentBinMovementMainBinding

    private lateinit var addButton: FloatingActionButton
    private lateinit var itemsBeingMovedRecyclerView:RecyclerView
    private lateinit var continueButton: Button
    private lateinit var progressDialog: Dialog

    private lateinit var addBinMovementToListPopupWindow: PopupWindow

    private lateinit var adapter: BinMovementAdapter
    private lateinit var itemNumberDropdownAdapter:CustomItemDropDownAdapter

    private val viewModel: BinMovementViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun fillItemNumberSpinnerWithItems( listOfItemsInBin:List<itemsInBin>){
        val itemsInBin = mutableListOf<itemsInBin>()
        for (anItem in listOfItemsInBin){
            itemsInBin.add(anItem)
        }
        itemNumberDropdownAdapter = CustomItemDropDownAdapter(requireContext(), listOfItemsInBin)
        val itemNumberSpinner = addBinMovementToListPopupWindow.contentView.findViewById<AutoCompleteTextView>(R.id.itemNumberSpinner)
        itemNumberSpinner.setAdapter(itemNumberDropdownAdapter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bin_movement_main, container, false)

        initUIElements()
        initObservers()


        // Gets the company id from the Shared Preferences
        val companyID:String = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        viewModel.setCompanyIDFromSharedPref(companyID)

        // Gets the company id from the Shared Preferences
        val warehouseNumber: Int = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
        viewModel.setWarehouseFromSharedPref(warehouseNumber)

        progressDialog.show()
        //getting all items from backend to fill the item number drop down
        viewModel.getAllItemsInAllBinsFromBackend()

        return binding.root
    }

    private fun clearAllItemsFromRecyclerView(){
        adapter.clearAllItems()
        adapter.notifyDataSetChanged()
    }

    private fun initObservers(){

        viewModel.allItemsMoved.observe(viewLifecycleOwner) { allMoved ->
            progressDialog.dismiss()
            if (allMoved) {
                // Reset the flag or handle as needed
                AlerterUtils.startSuccessAlert(requireActivity(), "Success","Successfully Moved items to respective bins")
                viewModel.resetAllItemsMovedFlag()
                clearAllItemsFromRecyclerView()
                progressDialog.show()
                viewModel.getAllItemsInAllBinsFromBackend()
            }

        }

        viewModel.wasItemMovedSuccessfully.observe(viewLifecycleOwner){wasItemMoved ->
            if (!wasItemMoved) {
                AlerterUtils.startErrorAlerter(
                    requireActivity(),
                    viewModel.errorMessage.value!!["moveItemBetweenBins"]!!
                )
            }

        }


        viewModel.listOfBinsInWarehouse.observe(viewLifecycleOwner){
        }

        viewModel.listOfAllItemsInAllBins.observe(viewLifecycleOwner){newListOfItemsInBin ->
            progressDialog.dismiss()
            val fromBinNumber = addBinMovementToListPopupWindow.contentView.findViewById<AutoCompleteTextView>(R.id.fromBinNumber)
            if(fromBinNumber.text.toString().isNotEmpty()) {
                fillItemNumberSpinnerWithItems(newListOfItemsInBin)
            }
        }
    }

    fun filterItemsByBinLocation(binLocation: String): List<itemsInBin> {
        return viewModel.listOfAllItemsInAllBins.value!!.filter { it.binLocation.lowercase() == binLocation.lowercase() }
    }



    private fun initAddItemToListPopup(){
        addBinMovementToListPopupWindow = PopupWindowUtils.getCustomPopup(requireContext(),R.layout.popup_bin_movement_add_new_item,)
        val popupContentView = addBinMovementToListPopupWindow.contentView

        val itemNumberSpinner = popupContentView.findViewById<AutoCompleteTextView>(R.id.itemNumberSpinner)
        itemNumberSpinner.setOnItemClickListener{ parent, view, position, id ->
            val selectedItem = itemNumberDropdownAdapter.getItem(position) as itemsInBin
            viewModel.setCurrentlyChosenItemToMove(selectedItem)
            itemNumberSpinner.setText(selectedItem.itemName,false)
        }

        val fromBinAutoCompleteTextView = popupContentView.findViewById<AutoCompleteTextView>(R.id.fromBinNumber)

        fromBinAutoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Code to execute before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Code to execute as text is changing
                val filteredList = filterItemsByBinLocation(s.toString())
                fillItemNumberSpinnerWithItems(filteredList)
                itemNumberSpinner.text.clear()
            }

            override fun afterTextChanged(s: Editable?) {
                // Code to execute after text has changed
            }
        })

        val toBinAutoCompleteTextView = popupContentView.findViewById<AutoCompleteTextView>(R.id.toBinNumber)

        val amountToMoveEditText = popupContentView.findViewById<EditText>(R.id.itemAmountEditText)

        val addButton = popupContentView.findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            val itemName = viewModel.currentlyChosenItemToMove.value!!.itemName
            val itemNumber = viewModel.currentlyChosenItemToMove.value!!.itemNumber
            val rowIDOfItem = viewModel.currentlyChosenItemToMove.value!!.rowID
            val quantityToMove = amountToMoveEditText.text.toString().toInt()
            val fromBin = fromBinAutoCompleteTextView.text.toString()
            val toBin = toBinAutoCompleteTextView.text.toString()
            val isAtLeastOneEditTextEmpty = verifyIfAtLeastOneEditTextIsEmpty(fromBin, toBin, quantityToMove.toString(), itemNumber)
            if(isAtLeastOneEditTextEmpty)
                AlerterUtils.startErrorAlerter(requireActivity(), "All fields must be filled")
            else {
                val quantityToBePicked =
                    viewModel.currentlyChosenItemToMove.value!!.quantityInPicking.toInt()
                val quantityOnHand =
                    viewModel.currentlyChosenItemToMove.value!!.quantityOnHand.toInt()
                val quantityAvailable = quantityOnHand - quantityToBePicked


                if (quantityToMove > (quantityOnHand - quantityToBePicked)) {
                    AlerterUtils.startErrorAlerter(
                        requireActivity(),
                        "Cannot move ${quantityToMove} units because there is only ${quantityAvailable} units available."
                    )
                } else {
                    val newItemToAdd =
                        BinMovementDataClass(itemName, itemNumber, rowIDOfItem ,quantityToMove, fromBin, toBin)
                    adapter.addItem(newItemToAdd)
                    clearAllBinMovementEditTextFromPopup()
                    addBinMovementToListPopupWindow.dismiss()
                }
            }
        }

    }


    private fun initUIElements(){

        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        initAddItemToListPopup()
        addButton = binding.addButton
        addButton.setOnClickListener{
            addBinMovementToListPopupWindow.showAtLocation(requireView(), Gravity.CENTER, 0, 0)
        }

        continueButton = binding.continueButton
        continueButton.setOnClickListener {
            val questionPopup = PopupWindowUtils.createQuestionPopup(requireContext(), "Are you sure you would like to commit the movements?", "Move Items?")
            questionPopup.contentView.findViewById<Button>(R.id.YesButton).setOnClickListener{
                progressDialog.show()
                viewModel.moveItemsBetweenBins(adapter.data)
                questionPopup.dismiss()
            }

            questionPopup.contentView.findViewById<Button>(R.id.NoButton).setOnClickListener{
                questionPopup.dismiss()
            }

            questionPopup.showAtLocation(requireView(), Gravity.CENTER, 0, 0)


        }
        adapter = BinMovementAdapter { hasItems ->
            continueButton.isEnabled = hasItems
        }

        itemsBeingMovedRecyclerView = binding.itemsBeingMovedRecyclerView
        itemsBeingMovedRecyclerView.adapter = adapter

    }

    private fun verifyIfAtLeastOneEditTextIsEmpty(fromBinNumber:String, toBinNumber:String, itemAmount:String, itemNumber:String):Boolean{
        return fromBinNumber.isEmpty() || toBinNumber.isEmpty() || itemAmount.isEmpty() || itemNumber.isEmpty()
    }

    private fun clearAllBinMovementEditTextFromPopup(){
        addBinMovementToListPopupWindow.contentView.findViewById<AutoCompleteTextView>(R.id.fromBinNumber).text.clear()
        addBinMovementToListPopupWindow.contentView.findViewById<AutoCompleteTextView>(R.id.toBinNumber).text.clear()
        addBinMovementToListPopupWindow.contentView.findViewById<EditText>(R.id.itemAmountEditText).text.clear()
        addBinMovementToListPopupWindow.contentView.findViewById<AutoCompleteTextView>(R.id.itemNumberSpinner).text.clear()
    }


    class CustomItemDropDownAdapter(context: Context, private var suggestions: List<itemsInBin>): ArrayAdapter<itemsInBin>(context, 0, suggestions){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.bin_movement_items_in_bin_suggestion_view, parent, false)
            val itemDescriptionTextView = view.findViewById<TextView>(R.id.itemDescriptionTextView)
            val itemNumberTextView = view.findViewById<TextView>(R.id.itemNumberTextView)
            val binLocationTextView = view.findViewById<TextView>(R.id.binLocationValueTextView)
            val quantityOnHandTextView = view.findViewById<TextView>(R.id.QuantityInHandValueTextView)
            val quantityInPickingTextView = view.findViewById<TextView>(R.id.QuantityInPickingValueTextView)
            val quantityAvailableToMoveTextView = view.findViewById<TextView>(R.id.QuantityAvailableValueTextView)

            val item = suggestions[position]
            itemDescriptionTextView.text = item.itemName
            itemNumberTextView.text = item.itemNumber
            binLocationTextView.text = item.binLocation
            quantityOnHandTextView.text = item.quantityOnHand.toString()
            quantityInPickingTextView.text = item.quantityInPicking.toString()
            quantityAvailableToMoveTextView.text = (item.quantityOnHand - item.quantityInPicking).toString()


            return view
        }
    }


}