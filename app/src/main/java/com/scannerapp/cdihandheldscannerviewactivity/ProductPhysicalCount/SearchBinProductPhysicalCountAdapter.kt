package com.comdist.cdihandheldscannerviewactivity.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.BinsByClassCodeByVendorAndByItemNumber
import com.scannerapp.cdihandheldscannerviewactivity.R

class BinItemAdapter(
    private val context: Context,
    private val onItemClick: (BinsByClassCodeByVendorAndByItemNumber) -> Unit
) : RecyclerView.Adapter<BinItemAdapter.ViewHolder>() {
    private var items: MutableList<BinsByClassCodeByVendorAndByItemNumber> = mutableListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binLocationTextView: TextView = view.findViewById(R.id.binLocationTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_physical_count_bin_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: BinsByClassCodeByVendorAndByItemNumber = items[position]
        holder.binLocationTextView.text = item.binLocation
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newData: List<BinsByClassCodeByVendorAndByItemNumber>) {
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()
    }

    fun getData(): List<BinsByClassCodeByVendorAndByItemNumber> {
        return items
    }
}
