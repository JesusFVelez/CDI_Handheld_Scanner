package com.comdist.cdihandheldscannerviewactivity.ItemPicking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemsInOrderInfo

class ItemPickingAdapter(private val listener: itemInOrderClickListener) : RecyclerView.Adapter<ItemPickingViewHolder>(){

    // Data source for the adapter
    var data = listOf<ItemsInOrderInfo>()
        set(value) {
            field = value
            notifyDataSetChanged()  // Notify the adapter that the data set has changed
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPickingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_in_order_view, parent, false)
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
        holder.remainingItemsToPickTextView.text = "Picked: " + item.quantityPicked.toInt() + "/" + item.totalQuantityToBePicked.toInt()


        when (item.itemPickingStatus) {
            "Picked" -> holder.itemPickingStatusImage.setImageResource(R.drawable.checkmark_icon)
            "Partially Picked" -> holder.itemPickingStatusImage.setImageResource(R.drawable.warning_icon)
            "Not Picked" -> holder.itemPickingStatusImage.setImageResource(R.drawable.error_icon)
            else -> holder.itemPickingStatusImage.setImageResource(R.drawable.black_warning_icon)
        }
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