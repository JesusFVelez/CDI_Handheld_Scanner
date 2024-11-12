package com.scannerapp.cdihandheldscannerviewactivity.EditItem

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemData
import com.scannerapp.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.scannerapp.cdihandheldscannerviewactivity.databinding.EditItemMainFragmentBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale


class EditItemMainFragment : Fragment() {
    private lateinit var binding: EditItemMainFragmentBinding
    private val viewModel: EditItemViewModel by activityViewModels()
    private lateinit var itemSuggestionAdapter: ItemSuggestionRecyclerViewAdapter

    private lateinit var progressDialog: Dialog



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.edit_item_main_fragment, container, false
        )

        setupUI()
        initObservers()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.itemNumberSearchEditText.text.clear()

    }
    override fun onPause() {
        super.onPause()

    }
    private fun setupUI() {
        // Initialize your adapter with the item click lambda

        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        itemSuggestionAdapter = ItemSuggestionRecyclerViewAdapter(requireContext()) { selectedItem ->
            viewModel.setCurrentlyChosenItemForSearch(selectedItem)
            navigateToEditItemDetailsFragment()
        }
        binding.itemSearchList.adapter = itemSuggestionAdapter

        // Setup AutoCompleteTextView
        progressDialog.show()
        viewModel.fetchItemSuggestions() // Assuming this will populate `viewModel.suggestions`
    }

    private fun navigateToEditItemDetailsFragment() {
        view?.findNavController()?.navigate(R.id.action_editItemMainFragment_to_editItemDetailsFragment)
    }

    private fun initObservers() {

        viewModel.suggestions.observe(viewLifecycleOwner) { newSuggestions ->
            progressDialog.dismiss()
            initItemNumberAutoCompleteTextView(newSuggestions)
        }


        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLasAPICallSuccessful ->
            if (!wasLasAPICallSuccessful) {
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity(), viewModel.networkErrorMessage.value!!)
            }
        }

        viewModel.suggestions.observe(viewLifecycleOwner) { newSuggestions ->
            progressDialog.dismiss()
            // This ensures the RecyclerView is updated with the initial data as soon as it's available.
            itemSuggestionAdapter.updateData(newSuggestions)
        }

        //commented for repetitive reasons but maybe useful in the future
        viewModel.itemsInBinFromBarcode.observe(viewLifecycleOwner) { itemsInBin ->
            progressDialog.dismiss()
            if (itemsInBin.isNotEmpty()) {
                itemSuggestionAdapter.updateData(itemsInBin)
            } else {
                AlerterUtils.startWarningAlerter(requireActivity(), "No items found for the given barcode.")
            }
        }

    }

    private fun initItemNumberAutoCompleteTextView(newItemSuggestion: List<ItemData>) {
        // Setup AutoCompleteTextView with the adapter
        val autoCompleteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, newItemSuggestion.map { it.itemNumber })
        (binding.itemNumberSearchEditText as? AutoCompleteTextView)?.apply {
            setAdapter(autoCompleteAdapter)
            threshold = 1

            // When an item is clicked in the suggestions, update the RecyclerView to show only that item
            onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedItemNumber = parent.getItemAtPosition(position) as String
                viewModel.setCurrentlyChosenItemForSearch(newItemSuggestion.first { it.itemNumber == selectedItemNumber })
                itemSuggestionAdapter.updateData(listOf(newItemSuggestion.first { it.itemNumber == selectedItemNumber }))
            }
        }


        binding.itemNumberSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val trimmedText = s.toString().trim()

                if (trimmedText.isEmpty()) {
                    itemSuggestionAdapter.updateData(newItemSuggestion)
                } else {
                    val filteredList = newItemSuggestion.filter {
                        it.itemNumber.contains(trimmedText, ignoreCase = true) ||
                                (it.barCode ?: "").contains(trimmedText, ignoreCase = true) ||
                                (it.vendorNumber ?: "").contains(trimmedText, ignoreCase = true) // New condition to filter by vendor number
                    }

                    if (filteredList.isEmpty()) {
                        progressDialog.show()
                        viewModel.getItemsInBinFromBarcode(trimmedText)
                    } else {
                        itemSuggestionAdapter.updateData(filteredList)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


    }

    fun ItemSuggestionRecyclerViewAdapter.updateData(newData: List<ItemData>) {
        // Assuming ItemSuggestionAdapter stores items in a List called 'items'
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()
    }

    class ItemSuggestionRecyclerViewAdapter(
        private val context: Context,
        private val onItemClick: (ItemData) -> Unit
    ) : RecyclerView.Adapter<ItemSuggestionRecyclerViewAdapter.ViewHolder>() {
        var items: MutableList<ItemData> = mutableListOf()

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            // Initialize view holder's views
            val itemNumberTextView: TextView = view.findViewById(R.id.ItemNumberTextView)
            val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
            val expirationDateTextView: TextView = view.findViewById(R.id.orderDateValueTextView)
            val binLocationTextView: TextView = view.findViewById(R.id.binLocationTextView)
            val lotNumberTextView: TextView = view.findViewById(R.id.dateWantedValueTextView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.edit_item_item_list_view, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.itemNumberTextView.text = item.itemNumber
            holder.descriptionTextView.text = item.itemDescription
            holder.expirationDateTextView.text = formatDateString(item.expireDate)
            holder.binLocationTextView.text = item.binLocation
            holder.lotNumberTextView.text = item.lotNumber ?: "N/A"
            // Set click listener for the entire ViewHolder
            holder.itemView.setOnClickListener {
                onItemClick(item) // Invoke the click lambda with the clicked item
            }
        }
        override fun getItemCount() = items.size

        // Update data and refresh list
        fun updateData(newData: List<ItemData>) {
            items.clear()
            items.addAll(newData)
            notifyDataSetChanged()
        }
        private fun formatDateString(dateString: String?): String {
            if (dateString == null || dateString.equals("null", ignoreCase = true) || dateString.isEmpty()) {
                return "N/A"
            }
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: "N/A"
            } catch (e: ParseException) {
                "N/A"
            }
        }
    }

}


