package com.example.cdihandheldscannerviewactivity.ItemPicking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cdihandheldscannerviewactivity.ProductsInBin.ProductsInBinItemOnClickListener
import com.example.cdihandheldscannerviewactivity.ProductsInBin.productInBinViewHolder
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.Network.ItemsInOrderInfo
import com.example.cdihandheldscannerviewactivity.Utils.Network.ProductInBinInfo
import org.w3c.dom.Text
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
        val item = data[position]
        holder.itemNumberTextView.text = item.itemNumber
        holder.itemNameTextView.text = item.itemName + " - " + item.itemDetails
        holder.binNumberTextView.text = item.binLocation
        holder.remainingItemsToPickTextView.text = "Picked: " + item.quantityPicked + "/" +item.totalQuantityToBePicked
        if(item.itemPickingStatus == "Picked")
            holder.itemPickingStatusImage.setImageResource(R.drawable.)
        else if(item.itemPickingStatus == "Partially Picked")
            holder.itemPickingStatusImage.setImageResource(R.drawable.warning_icon)


    }


}




class ItemPickingViewHolder(orderProductView: View, private val listener: itemInOrderClickListener):RecyclerView.ViewHolder(orderProductView), View.OnClickListener {
    val itemNumberTextView: TextView = orderProductView.findViewById(R.id.itemNumber)
    val itemNameTextView: TextView = orderProductView.findViewById(R.id.itemName)
    val itemPickingStatusImage: ImageView = orderProductView.findViewById(R.id.itemPickingStatusIcon)
    val binNumberTextView: TextView = orderProductView.findViewById(R.id.binNumber)
    val remainingItemsToPickTextView: TextView = orderProductView.findViewById(R.id.quantityToPick)

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