package com.scannerapp.cdihandheldscannerviewactivity.ReceivingProductsIntoWarehouse

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemsInBinList
import com.scannerapp.cdihandheldscannerviewactivity.Utils.PopupWindowUtils

class itemsInDoorBinAdapter(private val listener: itemInDoorBinClickListener, val onDataSetChanged: (Boolean) -> Unit, val removeItemFromDoorBinInBackend: (ItemsInBinList) -> Unit) : RecyclerView.Adapter<ItemsInDoorBinViewHolder>(){



    // Data source for the adapter
    var data = mutableListOf<ItemsInBinList>()
        set(value) {
            field = value
            notifyDataSetChanged()  // Notify the adapter that the data set has changed
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsInDoorBinViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.receiving_product_in_door_bin_list_view, parent, false)
        return ItemsInDoorBinViewHolder(view, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    //Method to remove an item
    fun removeItem(item: ItemsInBinList?){
        data.remove(item)
        removeItemFromDoorBinInBackend(item!!)
        notifyDataSetChanged()
        onDataSetChanged(data.isNotEmpty())
    }

    // Method to add an item
    fun addItems(items: MutableList<ItemsInBinList>?) {
        data = items!!
        notifyDataSetChanged()
        onDataSetChanged(data.isNotEmpty())
    }

    fun clearAllItems(){
        data = mutableListOf()
        notifyDataSetChanged()
        onDataSetChanged(data.isNotEmpty())
    }


    override fun onBindViewHolder(holder: ItemsInDoorBinViewHolder, position: Int) {
        val item = data[position]
        holder.itemNumberTextView.text = item.itemNumber
        holder.itemNameTextView.text = item.itemName
        holder.binLocationTextView.text = item.binLocation
        holder.lotNumberTextView.text = if (item.lotNumber == "") "N/A" else item.lotNumber


        if(item.isQtyLessOrMoreThanPOQty)
            holder.backgroundView.setBackgroundResource(R.drawable.warning_element_in_list)

        if(item.wasItemAlreadyReceived){
            holder.removeItemButton.setImageResource(R.drawable.checkmark_icon)
            holder.quantityInDoorBinTextView.text = "Already \n Moved"
            holder.expirationDateTextView.text = item.qtyOnHand.toString()
            holder.expirationDateHeaderTextView.text = "Moved:"

        }else {
            holder.expirationDateTextView.text = item.expirationDate
            holder.quantityInDoorBinTextView.text = "Added: " + item.qtyOnHand.toInt().toString()
            holder.removeItemButton.setOnClickListener {
                // Create popup window to ask whether user wants to delete item or not
                val popupWindow = PopupWindowUtils.createQuestionPopup(
                    it.context,
                    "Are you sure you want to delete item from the list?",
                    "Delete Item"
                )
                popupWindow.contentView.findViewById<Button>(R.id.YesButton).setOnClickListener {
                    removeItem(item)
                    popupWindow.dismiss()
                }
                popupWindow.contentView.findViewById<Button>(R.id.NoButton).setOnClickListener {
                    popupWindow.dismiss()
                }
                popupWindow.showAtLocation(it.rootView, Gravity.CENTER, 0, 0)
            }
        }

    }


}


class ItemsInDoorBinViewHolder(itemInDoorBinView: View, private val listener: itemInDoorBinClickListener):RecyclerView.ViewHolder(itemInDoorBinView), View.OnClickListener {
    val itemNumberTextView: TextView = itemInDoorBinView.findViewById(R.id.itemNumberText)
    val itemNameTextView: TextView = itemInDoorBinView.findViewById(R.id.productNameText)
    val binLocationTextView: TextView = itemInDoorBinView.findViewById(R.id.binNumberText)
    val lotNumberTextView: TextView = itemInDoorBinView.findViewById(R.id.lotNumberText)
    val expirationDateTextView: TextView = itemInDoorBinView.findViewById(R.id.expDateInfoText)
    val expirationDateHeaderTextView: TextView = itemInDoorBinView.findViewById(R.id.expDateHeaderText)
    val quantityInDoorBinTextView: TextView = itemInDoorBinView.findViewById(R.id.addedText)
    val removeItemButton: ImageButton = itemInDoorBinView.findViewById(R.id.trashcanIconButton)
    val backgroundView: ConstraintLayout = itemInDoorBinView.findViewById(R.id.ConstraintLayoutView)

    init{
        itemInDoorBinView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        listener.onItemClickListener(view, adapterPosition)
    }

}

interface itemInDoorBinClickListener{
    fun onItemClickListener(view: View, position: Int)
}