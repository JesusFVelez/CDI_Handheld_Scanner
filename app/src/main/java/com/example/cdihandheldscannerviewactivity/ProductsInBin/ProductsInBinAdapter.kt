package com.example.cdihandheldscannerviewactivity.ProductsInBin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Network.ProductInBinInfo


class ProductsInBinAdapter(private val listener: ProductsInBinItemOnClickListener): RecyclerView.Adapter<productInBinViewHolder>() {
    var data = listOf<ProductInBinInfo>()

        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): productInBinViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.product_in_bin_view, parent, false)
        return productInBinViewHolder(view, listener)
    }

//    fun clear() {
//        val size = data.size
//        data = listOf()
//        notifyItemRangeRemoved(0, size)
//    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: productInBinViewHolder, position: Int) {
        val item = data[position]
        holder.productNumberTextView.text = item.itemNumber
        holder.productNameTextView.text = "${item.itemName} - ${item.itemDetails}"
        holder.onHandQtyTextView.text = "x${item.quantityOnHand.toInt().toString()}"
    }

}


data class producstInBinViewData(
    val productNumber: String,
    val productName: String,
    val onHandQty: Int
)
class productInBinViewHolder(productView: View, private val listener: ProductsInBinItemOnClickListener): RecyclerView.ViewHolder(productView), View.OnClickListener{
    val productNumberTextView: TextView = productView.findViewById(R.id.productNumberText)
    val productNameTextView: TextView = productView.findViewById(R.id.productNameText)
    val onHandTextView: TextView = productView.findViewById(R.id.onHandText)
    val onHandQtyTextView: TextView = productView.findViewById(R.id.onHandQtyText)

    init {
        productView.setOnClickListener(this)
    }
    fun bind(data: producstInBinViewData){
        productNameTextView.text = data.productName
        productNumberTextView.text = data.productName
        onHandQtyTextView.text = data.onHandQty.toString()
    }

    override fun onClick(v: View) {
        listener.onItemClickListener(v, adapterPosition)
    }


}


interface ProductsInBinItemOnClickListener{
    fun onItemClickListener(view: View, position: Int)
}

