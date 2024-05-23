package com.scannerapp.cdihandheldscannerviewactivity.ProductPhysicalCount

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.comdist.cdihandheldscannerviewactivity.InventoryCount.InventoryCountViewModel
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.TtItemInfo
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.scannerapp.cdihandheldscannerviewactivity.databinding.ProductPhysicalCountItemListFragmentBinding

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
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.itemSearchEditText.text.clear()
        fetchItemsInBin()
    }

    private fun setupUI() {
        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        itemAdapter = ItemAdapter(requireContext(), viewModel) { selectedItem ->
            // Handles item click if needed for future use
        }
        binding.itemSearchList.adapter = itemAdapter

        binding.itemSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val trimmedText = s.toString().trim()
                filterItemList(trimmedText)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchItemsInBin() {
        val selectedBin = viewModel.setCurrentlySelectedBin.value
        val warehouseNO = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
        viewModel.setWarehouseNumberFromSharedPref(warehouseNO)

        val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        viewModel.setCompanyIDFromSharedPref(companyID)
        if (selectedBin != null) {
            progressDialog.show()
            viewModel.getAllItemsInBinForSuggestion(
                pBinLocation = selectedBin.binLocation,
                pWarehouse = warehouseNO,
                pCompanyID = companyID
            )
        }
    }

    private fun initObservers() {
        viewModel.itemInfo.observe(viewLifecycleOwner) { newItemInfo ->
            progressDialog.dismiss()
            itemAdapter.updateData(newItemInfo)
        }

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLastAPICallSuccessful ->
            if (!wasLastAPICallSuccessful) {
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity())
            }
        }
    }

    private fun filterItemList(query: String) {
        val filteredList = viewModel.itemInfo.value?.filter { item ->
            item.itemNumber.contains(query, ignoreCase = true) ||
                    item.barCode?.contains(query, ignoreCase = true) == true
        }
        itemAdapter.updateData(filteredList ?: listOf())
    }

    class ItemAdapter(
        private val context: Context,
        private val viewModel: InventoryCountViewModel,
        private val onItemClick: (TtItemInfo) -> Unit
    ) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
        var items: MutableList<TtItemInfo> = mutableListOf()

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemNumberTextView: TextView = view.findViewById(R.id.ItemNumberTextView)
            val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
            val expirationDateTextView: TextView = view.findViewById(R.id.orderDateValueTextView)
            val binLocationTextView: TextView = view.findViewById(R.id.binLocationTextView)
            val lotNumberTextView: TextView = view.findViewById(R.id.dateWantedValueTextView)
            val countedTextView: TextView = view.findViewById(R.id.CountedTextView)
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

            if (item.inCount) {
                holder.countedTextView.visibility = View.VISIBLE
            } else {
                holder.countedTextView.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                showPopupDialog(item)
                onItemClick(item)
            }
        }

        override fun getItemCount() = items.size

        fun updateData(newData: List<TtItemInfo>) {
            items.clear()
            items.addAll(newData)
            notifyDataSetChanged()
        }

        private fun showPopupDialog(item: TtItemInfo) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.product_physical_count_item_quantity_count_popup, null)
            val dialog = Dialog(context)
            dialog.setContentView(dialogView)

            val selectedItemView = dialogView.findViewById<View>(R.id.selectedItem)
            val itemNumberTextView = selectedItemView.findViewById<TextView>(R.id.ItemNumberTextView)
            val descriptionTextView = selectedItemView.findViewById<TextView>(R.id.descriptionTextView)
            val expirationDateTextView = selectedItemView.findViewById<TextView>(R.id.orderDateValueTextView)
            val binLocationTextView = selectedItemView.findViewById<TextView>(R.id.binLocationTextView)
            val lotNumberTextView = selectedItemView.findViewById<TextView>(R.id.dateWantedValueTextView)

            itemNumberTextView.text = item.itemNumber
            descriptionTextView.text = item.itemDescription
            expirationDateTextView.text = item.expireDate ?: "N/A"
            binLocationTextView.text = item.binLocation
            lotNumberTextView.text = item.lotNumber ?: "N/A"

            val itemAmountEditText = dialogView.findViewById<EditText>(R.id.itemAmountEditText)
            if (item.inCount) {
                itemAmountEditText.setText(item.qtyCounted?.toString() ?: "0")
            }

            dialogView.findViewById<AppCompatButton>(R.id.addButton).setOnClickListener {
                val quantity = itemAmountEditText.text.toString().toIntOrNull()
                if (quantity != null && quantity >= 0) {
                    val warehouseNO = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(context)
                    val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(context)
                    viewModel.updateCount(
                        pItemNumber = item.itemNumber,
                        pWarehouseNo = warehouseNO,
                        pBinLocation = item.binLocation,
                        pQtyCounted = quantity.toDouble(),
                        pCompanyID = companyID
                    )
                    dialog.dismiss()
                    // Refresh the list after updating the count
                    viewModel.getAllItemsInBinForSuggestion(
                        pBinLocation = item.binLocation,
                        pWarehouse = warehouseNO,
                        pCompanyID = companyID
                    )
                } else {
                    itemAmountEditText.error = "Please enter a valid number"
                }
            }

            dialog.show()
        }
    }
}

