package com.scannerapp.cdihandheldscannerviewactivity.ProductPhysicalCount
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.comdist.cdihandheldscannerviewactivity.InventoryCount.InventoryCountViewModel
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemData
import com.scannerapp.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.scannerapp.cdihandheldscannerviewactivity.databinding.ProductPhysicalCountItemListFragmentBinding

/*class EditAndSearchItemProductPhysicalCountFragment : Fragment() {
    private lateinit var binding: ProductPhysicalCountItemListFragmentBinding
    private val viewModel: AssignExpirationDateAndLotNumberViewModel by activityViewModels()
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var progressDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.product_physical_count_item_list_fragment, container, false
        )
        setupUI()
        initObservers()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.itemSearchEditText.text.clear()
    }

    private fun setupUI() {
        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        itemAdapter = ItemAdapter(requireContext()) { selectedItem ->
            viewModel.setCurrentlyChosenItemForSearch(selectedItem)
            // Handle item click if needed
        }
        binding.itemSearchList.adapter = itemAdapter

        progressDialog.show()
        viewModel.fetchItemSuggestions() //just to see if it works
    }

    private fun initObservers() {
        viewModel.suggestions.observe(viewLifecycleOwner) { newSuggestions ->
            progressDialog.dismiss()
            initItemNumberAutoCompleteTextView(newSuggestions)
            itemAdapter.updateData(newSuggestions)
        }

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLastAPICallSuccessful ->
            if (!wasLastAPICallSuccessful) {
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity())
            }
        }
    }

    private fun initItemNumberAutoCompleteTextView(newItemSuggestion: List<ItemData>) {
        val autoCompleteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, newItemSuggestion.map { it.itemNumber })
        (binding.itemSearchEditText as? AutoCompleteTextView)?.apply {
            setAdapter(autoCompleteAdapter)
            threshold = 1

            onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedItemNumber = parent.getItemAtPosition(position) as String
                val selectedItem = newItemSuggestion.first { it.itemNumber == selectedItemNumber }
                itemAdapter.updateData(listOf(selectedItem))
                viewModel.setCurrentlyChosenItemForSearch(selectedItem)
            }
        }

        binding.itemSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val trimmedText = s.toString().trim()
                if (trimmedText.isEmpty()) {
                    itemAdapter.updateData(newItemSuggestion)
                } else {
                    val filteredList = newItemSuggestion.filter {
                        it.itemNumber.contains(trimmedText, ignoreCase = true) ||
                                (it.barCode ?: "").contains(trimmedText, ignoreCase = true)
                    }
                    itemAdapter.updateData(filteredList)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    class ItemAdapter(
        private val context: Context,
        private val onItemClick: (ItemData) -> Unit
    ) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
        var items: MutableList<ItemData> = mutableListOf()

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemNumberTextView: TextView = view.findViewById(R.id.ItemNumberTextView)
            val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
            val expirationDateTextView: TextView = view.findViewById(R.id.orderDateValueTextView)
            val binLocationTextView: TextView = view.findViewById(R.id.binLocationTextView)
            val lotNumberTextView: TextView = view.findViewById(R.id.dateWantedValueTextView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.product_physical_count_item_list_view, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.itemNumberTextView.text = item.itemNumber
            holder.descriptionTextView.text = item.itemDescription
            holder.expirationDateTextView.text = item.expireDate ?: "N/A"
            holder.binLocationTextView.text = item.binLocation
            holder.lotNumberTextView.text = item.lotNumber ?: "N/A"
            holder.itemView.setOnClickListener {
                onItemClick(item)
            }
        }

        override fun getItemCount() = items.size

        fun updateData(newData: List<ItemData>) {
            items.clear()
            items.addAll(newData)
            notifyDataSetChanged()
        }
    }
}*/



class EditAndSearchItemProductPhysicalCountFragment : Fragment() {
    private lateinit var binding: ProductPhysicalCountItemListFragmentBinding
    private val viewModel: InventoryCountViewModel by activityViewModels()
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var progressDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.product_physical_count_item_list_fragment, container, false
        )
        setupUI()
        initObservers()
        // Retrieve company ID and warehouse number from shared preferences
        val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        val warehouseNumber = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())

        // Set these values in the ViewModel
        viewModel.setCompanyIDFromSharedPref(companyID)
        viewModel.setWarehouseNumberFromSharedPref(warehouseNumber)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.itemSearchEditText.text.clear()
        fetchItemsInBin()
    }

    private fun setupUI() {
        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        /*itemAdapter = ItemAdapter(requireContext()) { selectedItem ->
            viewModel.setCurrentlySelectedBin(selectedItem)
            // Handle item click if needed
        }*/
        binding.itemSearchList.adapter = itemAdapter
    }

    private fun fetchItemsInBin() {
        val selectedBin = viewModel.setCurrentlySelectedBin.value
        val warehouseNO = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
        viewModel.setWarehouseNumberFromSharedPref(warehouseNO)

        val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        viewModel.setCompanyIDFromSharedPref(companyID)
        if (selectedBin != null) {
            progressDialog.show()
            viewModel.getAllItemsInBinForSuggestion(pBinLocation = selectedBin.binLocation, warehouseNO,companyID)
        }
    }

    private fun initObservers() {
        viewModel.itemInfo.observe(viewLifecycleOwner) { newItemInfo ->
            progressDialog.dismiss()

        }

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLastAPICallSuccessful ->
            if (!wasLastAPICallSuccessful) {
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity())
            }
        }
    }

    class ItemAdapter(
        private val context: Context,
        private val onItemClick: (ItemData) -> Unit
    ) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
        var items: MutableList<ItemData> = mutableListOf()

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemNumberTextView: TextView = view.findViewById(R.id.ItemNumberTextView)
            val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
            val expirationDateTextView: TextView = view.findViewById(R.id.orderDateValueTextView)
            val binLocationTextView: TextView = view.findViewById(R.id.binLocationTextView)
            val lotNumberTextView: TextView = view.findViewById(R.id.dateWantedValueTextView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.product_physical_count_item_list_view, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.itemNumberTextView.text = item.itemNumber
            holder.descriptionTextView.text = item.itemDescription
            holder.expirationDateTextView.text = item.expireDate ?: "N/A"
            holder.binLocationTextView.text = item.binLocation
            holder.lotNumberTextView.text = item.lotNumber ?: "N/A"
            holder.itemView.setOnClickListener {
                onItemClick(item)
            }
        }

        override fun getItemCount() = items.size

        fun updateData(newData: List<ItemData>) {
            items.clear()
            items.addAll(newData)
            notifyDataSetChanged()
        }
    }
}
