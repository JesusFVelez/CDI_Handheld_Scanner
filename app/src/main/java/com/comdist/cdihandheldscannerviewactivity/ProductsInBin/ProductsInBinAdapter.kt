package com.comdist.cdihandheldscannerviewactivity.ProductsInBin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ProductInBinInfo


// Adapter class for the RecyclerView displaying products in a bin
class ProductsInBinAdapter(private val listener: ProductsInBinItemOnClickListener): RecyclerView.Adapter<productInBinViewHolder>() {

    // Data source for the adapter
    var data = listOf<ProductInBinInfo>()
        set(value) {
            field = value
            notifyDataSetChanged()  // Notify the adapter that the data set has changed
        }

    // Create new ViewHolder instances
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): productInBinViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.product_in_bin_view, parent, false)
        return productInBinViewHolder(view, listener)
    }

    // TODO: Uncomment this function if you want to clear the data in the adapter
    // fun clear() {
    //     val size = data.size
    //     data = listOf()
    //     notifyItemRangeRemoved(0, size)
    // }

    // Get the size of the data set
    override fun getItemCount(): Int {
        return data.size
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: productInBinViewHolder, position: Int) {
        val item = data[position]
        holder.productNumberTextView.text = item.itemNumber
        holder.productNameTextView.text = "${item.itemName} - ${item.itemDetails}"
        holder.onHandQtyTextView.text = "x${item.quantityOnHand.toInt().toString()}"
    }
}

// Data class to hold the view data for each product in the bin
data class producstInBinViewData(
    val productNumber: String,
    val productName: String,
    val onHandQty: Int
)

// ViewHolder class for the RecyclerView
class productInBinViewHolder(productView: View, private val listener: ProductsInBinItemOnClickListener): RecyclerView.ViewHolder(productView), View.OnClickListener {

    // UI elements in each item view
    val productNumberTextView: TextView = productView.findViewById(R.id.productNumberText)
    val productNameTextView: TextView = productView.findViewById(R.id.productNameText)
    val onHandTextView: TextView = productView.findViewById(R.id.onHandText)
    val onHandQtyTextView: TextView = productView.findViewById(R.id.onHandQtyText)

    // Initialize the ViewHolder and set its onClickListener
    init {
        productView.setOnClickListener(this)
    }

    // Bind data to the ViewHolder's UI elements
    fun bind(data: producstInBinViewData){
        productNameTextView.text = data.productName
        productNumberTextView.text = data.productName
        onHandQtyTextView.text = data.onHandQty.toString()
    }

    // Handle item click events
    override fun onClick(v: View) {
        listener.onItemClickListener(v, adapterPosition)
    }
}

// Interface for handling item click events in the RecyclerView
interface ProductsInBinItemOnClickListener {
    fun onItemClickListener(view: View, position: Int)
}

