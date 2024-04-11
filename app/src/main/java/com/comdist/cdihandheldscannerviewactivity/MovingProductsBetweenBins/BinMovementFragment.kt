package com.comdist.cdihandheldscannerviewactivity.MovingProductsBetweenBins

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.compose.ui.text.toUpperCase
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.itemsInBin
import com.comdist.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.comdist.cdihandheldscannerviewactivity.databinding.FragmentBinMovementBinding


class BinMovementFragment : Fragment() {

    private lateinit var binding: FragmentBinMovementBinding

    private lateinit var itemNumberSpinner: AutoCompleteTextView
    private lateinit var itemAmountEditText: EditText
    private lateinit var fromBinNumber:EditText
    private lateinit var toBinNumber:EditText
    private lateinit var addButton:Button
    private lateinit var itemsBeingMovedRecyclerView:RecyclerView
    private lateinit var continueButton: Button
    private lateinit var clearButton: Button

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
        itemNumberSpinner.setAdapter(itemNumberDropdownAdapter)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bin_movement, container, false)

        initUIElements()
        initObservers()


        // Gets the company id from the Shared Preferences
        val companyID:String = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        viewModel.setCompanyIDFromSharedPref(companyID)

        // Gets the company id from the Shared Preferences
        val warehouseNumber: Int = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
        viewModel.setWarehouseFromSharedPref(warehouseNumber)

        //getting all items from backend to fill the item number drop down
        viewModel.getAllItemsInAllBinsFromBackend()

        return binding.root
    }

    private fun initObservers(){
        viewModel.listOfBinsInWarehouse.observe(viewLifecycleOwner){
        }

        viewModel.listOfAllItemsInAllBins.observe(viewLifecycleOwner){newListOfItemsInBin ->
            if(fromBinNumber.text.toString().isNotEmpty())
                fillItemNumberSpinnerWithItems( newListOfItemsInBin)
        }
    }

    fun filterItemsByBinLocation(binLocation: String): List<itemsInBin> {
        return viewModel.listOfAllItemsInAllBins.value!!.filter { it.binLocation == binLocation }
    }

    private fun initUIElements(){
        itemNumberSpinner = binding.itemNumberSpinner
        itemNumberSpinner.setOnItemClickListener{ parent, view, position, id ->
            val selectedItem = itemNumberDropdownAdapter.getItem(position) as itemsInBin
            viewModel.setCurrentlyChosenItemToMove(selectedItem)
            itemNumberSpinner.setText(selectedItem.itemNumber.uppercase(),false)
        }
        itemAmountEditText = binding.itemAmountEditText
        fromBinNumber = binding.fromBinNumber
        fromBinNumber.addTextChangedListener(object : TextWatcher {
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

        toBinNumber = binding.toBinNumber
        addButton = binding.addButton
        addButton.setOnClickListener{
            val isAtLeastOneEditTextEmpty = verifyIfAtLeastOneEditTextIsEmpty()
            if(isAtLeastOneEditTextEmpty)
                AlerterUtils.startErrorAlerter(requireActivity(), "All fields must be filled")
            else {
                val itemName = viewModel.currentlyChosenItemToMove.value!!.itemName
                val itemNumber = viewModel.currentlyChosenItemToMove.value!!.itemNumber
                val rowIDOfItem = viewModel.currentlyChosenItemToMove.value!!.rowID
                val quantityToMove = itemAmountEditText.text.toString().toInt()
                val fromBin = fromBinNumber.text.toString()
                val toBin = toBinNumber.text.toString()


                val quantityToBePicked =
                    viewModel.currentlyChosenItemToMove.value!!.quantityInPicking.toInt()
                val quantityOnHand =
                    viewModel.currentlyChosenItemToMove.value!!.quantityOnHand.toInt()
                val quantityAvailable = quantityOnHand - quantityToBePicked


                if (quantityToMove >= (quantityOnHand - quantityToBePicked)) {
                    AlerterUtils.startErrorAlerter(
                        requireActivity(),
                        "Cannot move ${quantityToMove} units because there is only ${quantityAvailable} units available."
                    )
                } else {
                    val newItemToAdd =
                        BinMovementDataClass(itemName, itemNumber, rowIDOfItem ,quantityToMove, fromBin, toBin)
                    adapter.addItem(newItemToAdd)
                    clearAllBinMovementEditText()
                }
            }
        }

        continueButton = binding.continueButton
        continueButton.setOnClickListener {
            for (items in adapter.data){

            }
        }


        clearButton = binding.clearButton
        clearButton.setOnClickListener { clearAllBinMovementEditText() }

        adapter = BinMovementAdapter { hasItems ->
            continueButton.isEnabled = hasItems
        }

        itemsBeingMovedRecyclerView = binding.itemsBeingMovedRecyclerView
        itemsBeingMovedRecyclerView.adapter = adapter


    }

    private fun verifyIfAtLeastOneEditTextIsEmpty():Boolean{
        return fromBinNumber.text.isEmpty() || toBinNumber.text.isEmpty() || itemAmountEditText.text.isEmpty() || itemNumberSpinner.text.isEmpty()
    }

    private fun clearAllBinMovementEditText(){
        fromBinNumber.text.clear()
        toBinNumber.text.clear()
        itemAmountEditText.text.clear()
        itemNumberSpinner.text.clear()
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