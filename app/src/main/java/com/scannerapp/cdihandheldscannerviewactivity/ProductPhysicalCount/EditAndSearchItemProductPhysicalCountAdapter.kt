package com.scannerapp.cdihandheldscannerviewactivity.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.comdist.cdihandheldscannerviewactivity.InventoryCount.InventoryCountViewModel
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.TtItemInfo
import com.scannerapp.cdihandheldscannerviewactivity.ProductPhysicalCount.EditAndSearchItemProductPhysicalCountFragment
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils

class ItemAdapter(
    private val context: Context,
    private val viewModel: InventoryCountViewModel,
    private val fragment: EditAndSearchItemProductPhysicalCountFragment,
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
            val warehouseNO = SharedPreferencesUtils.getWarehouseNumberFromSharedPref(context)
            val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(context)
            viewModel.getItemDetailsForPopup(item.itemNumber, warehouseNO, companyID)
            onItemClick(item)
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newData: List<TtItemInfo>) {
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()
    }
}
