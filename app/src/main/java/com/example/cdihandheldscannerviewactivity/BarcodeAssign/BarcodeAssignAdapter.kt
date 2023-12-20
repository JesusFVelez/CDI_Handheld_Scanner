package com.example.cdihandheldscannerviewactivity.BarcodeAssign

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cdihandheldscannerviewactivity.Utils.Network.BinInfo
import com.example.cdihandheldscannerviewactivity.R

class BarcodeAssignAdapter (): RecyclerView.Adapter<BarcodeAssignViewHolder>() {

    var data = listOf<ItemInfo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: int) BarcodeAssignViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.fragment_assign_barcode_to_product)
        return BarcodeAssignViewHolder(view)
    }

    override fun onBindViewHolder(holder: BarcodeAssignViewHolder, position: Int) {
        val product = data[position]
        val viewData = barcodeAssignViewData(product.itemNumber, product.itemName, product.ItemBarcode)
    }

}


// Object to store data from backend
data class barcodeAssignViewData (
    val itemNumber: String,
    val itemName: String,
    val itemBarcode: String
)

class BarcodeAssignViewHolder (barcodeAssignView: View): RecyclerView.ViewHolder(barcodeAssignView) {
    val itemNumberTextView: TextView = barcodeAssignView.findViewById(R.id.itemNumberEditText)
    val itemNameTextView: TextView = barcodeAssignView.findViewById(R.id.itemName)
    val itemBarcodeTextView: TextView = barcodeAssignView.findViewById(R.id.currentBarcodeText)

    init {}

    // Assigns values to GUI's elements
    fun bind(data: barcodeAssignViewData) {
        itemBarcodeTextView.text = when(data.itemBarcode) {
            "null" -> "Not yet Assigned"
            else -> data.itemBarcode
        }
        itemNameTextView.text = data.itemName
        itemNumberTextView.text = data.itemNumber

    }
}