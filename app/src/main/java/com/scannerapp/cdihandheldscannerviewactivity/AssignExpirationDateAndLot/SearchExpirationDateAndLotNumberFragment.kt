package com.scannerapp.cdihandheldscannerviewactivity.AssignExpirationDateAndLot

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


class SearchExpirationDateAndLotNumberFragment : Fragment() {
    private lateinit var binding: EditItemMainFragmentBinding
    private val viewModel: AssignExpirationDateAndLotNumberViewModel by activityViewModels()
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
            navigateToAssignExpirationDateAndLotNumberFragment()
        }
        binding.itemSearchList.adapter = itemSuggestionAdapter

        // Setup AutoCompleteTextView
        progressDialog.show()
        viewModel.fetchItemSuggestions() // Assuming this will populate `viewModel.suggestions`
    }

    private fun navigateToAssignExpirationDateAndLotNumberFragment() {
        view?.findNavController()?.navigate(R.id.action_SearchExpirationDateAndLotNumberFragment_to_AssignExpirationDateAndLotNumberFragment)
    }

    private fun initObservers() {

        viewModel.suggestions.observe(viewLifecycleOwner) { newSuggestions ->
            progressDialog.dismiss()
            initItemNumberAutoCompleteTextView(newSuggestions)
        }


        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLasAPICallSuccessful ->
            if (!wasLasAPICallSuccessful) {
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity())
            }
        }

        viewModel.suggestions.observe(viewLifecycleOwner) { newSuggestions ->
            progressDialog.dismiss()
            // This ensures the RecyclerView is updated with the initial data as soon as it's available.
            itemSuggestionAdapter.updateData(newSuggestions)
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
                val trimmedText = s.toString().trim() // Trim the text to remove leading and trailing spaces

                // This condition checks if the text has been cleared and resets to the full list.
                if (trimmedText.isEmpty()) {
                    itemSuggestionAdapter.updateData(newItemSuggestion) // Reset to full list if search is cleared
                } else {
                    // Filter the current list without making new API calls.
                    val filteredList = newItemSuggestion.filter {
                        it.itemNumber.contains(trimmedText, ignoreCase = true) ||
                                (it.barCode ?: "").contains(trimmedText, ignoreCase = true)
                    }
                    itemSuggestionAdapter.updateData(filteredList)

                    // If necessary, fetch new suggestions dynamically as the user types or scans a barcode.
                    // viewModel.fetchItemSuggestions(trimmedText)
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
            holder.expirationDateTextView.text = item.expireDate ?: "N/A"
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
    }

}


