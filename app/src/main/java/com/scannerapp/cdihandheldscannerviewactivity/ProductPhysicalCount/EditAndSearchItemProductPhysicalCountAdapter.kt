package com.scannerapp.cdihandheldscannerviewactivity.Adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.comdist.cdihandheldscannerviewactivity.InventoryCount.InventoryCountViewModel
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.TtItemInf
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.TtItemInfo
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils

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
        val qtyCountedTextView: TextView = view.findViewById(R.id.CountedTextView)
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
            holder.qtyCountedTextView.visibility = View.VISIBLE
            holder.qtyCountedTextView.text = "Counted: ${item.qtyCounted}" // Set the qtyCounted text
        } else {
            holder.countedTextView.visibility = View.GONE
            holder.qtyCountedTextView.visibility = View.GONE
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
                    pItemNumberOrBarCode = item.itemNumber,
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

    fun showPopupDialogForItemSearch(item: TtItemInf) {
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
        // Here, instead of using item.binLocation, use the selected bin location
        val selectedBinLocation = viewModel.setCurrentlySelectedBin.value?.binLocation ?: "Unknown"
        binLocationTextView.text = selectedBinLocation
        lotNumberTextView.text = item.lotNumber ?: "N/A"

        val itemAmountEditText = dialogView.findViewById<EditText>(R.id.itemAmountEditText)
        if (item.qtyCounted != null) {
            itemAmountEditText.setText(item.qtyCounted.toString())
        }

        dialogView.findViewById<AppCompatButton>(R.id.addButton).setOnClickListener {
            val quantity = itemAmountEditText.text.toString().toIntOrNull()
            if (quantity != null && quantity >= 0) {
                val warehouseNO = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(context)
                val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(context)
                viewModel.updateCount(
                    pItemNumberOrBarCode = item.itemNumber,
                    pWarehouseNo = warehouseNO,
                    pBinLocation = selectedBinLocation, // Use the selected bin location here
                    pQtyCounted = quantity.toDouble(),
                    pCompanyID = companyID
                )
                dialog.dismiss()
                // Refresh the list after updating the count
                viewModel.getAllItemsInBinForSuggestion(
                    pBinLocation = selectedBinLocation, // Use the selected bin location here
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
