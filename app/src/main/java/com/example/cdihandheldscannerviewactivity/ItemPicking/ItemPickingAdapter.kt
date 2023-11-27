package com.example.cdihandheldscannerviewactivity.ItemPicking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cdihandheldscannerviewactivity.ProductsInBin.ProductsInBinItemOnClickListener
import com.example.cdihandheldscannerviewactivity.ProductsInBin.productInBinViewHolder
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.Network.ItemsInOrderInfo
import com.example.cdihandheldscannerviewactivity.Utils.Network.ProductInBinInfo
import java.text.FieldPosition

class ItemPickingAdapter(private val listener: itemInOrderClickListener) : RecyclerView.Adapter<ItemPickingViewHolder>(){

    // Data source for the adapter
    var data = listOf<ItemsInOrderInfo>()
        set(value) {
            field = value
            notifyDataSetChanged()  // Notify the adapter that the data set has changed
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPickingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout., parent, false)
        return ItemPickingViewHolder(view, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ItemPickingViewHolder, position: Int) {

    }


}




class ItemPickingViewHolder(orderProductView: View, private val listener: itemInOrderClickListener):RecyclerView.ViewHolder(orderProductView), View.OnClickListener {


    init{
        orderProductView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        listener.onItemClickListener(view, adapterPosition)
    }

}



interface itemInOrderClickListener{
    fun onItemClickListener(view: View, position: Int)
}