package com.comdist.cdihandheldscannerviewactivity.ReceivingProductsIntoBin

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.comdist.cdihandheldscannerviewactivity.ItemPicking.ItemPickingViewHolder
import com.comdist.cdihandheldscannerviewactivity.ItemPicking.itemInOrderClickListener
import com.comdist.cdihandheldscannerviewactivity.MovingProductsBetweenBins.BinMovementDataClass
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemsInOrderInfo
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.confirmBinResponse
import com.comdist.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import java.text.SimpleDateFormat
import java.util.Locale

class itemsInDoorBinAdapter(private val listener: itemInDoorBinClickListener, val onDataSetChanged: (Boolean) -> Unit, val removeItemFromDoorBinInBackend: (ItemInDoorBinDataClass) -> Unit) : RecyclerView.Adapter<ItemsInDoorBinViewHolder>(){

    data class ItemInDoorBinDataClass(
        val expirationDate: String,
        val itemName: String,
        val binToBeMovedTo:String,
        val doorBin: String,
        val itemNumber:String,
        val lotNumber:String,
        val quantityOfItemsAddedToDoorBin:Int,
        val canEditLotNumber: Boolean,
        val rowIDForDoorBin: String)

    // Data source for the adapter
    var data = mutableListOf<ItemInDoorBinDataClass>()
        set(value) {
            field = value
            notifyDataSetChanged()  // Notify the adapter that the data set has changed
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsInDoorBinViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.view_receiving_product_in_door_bin, parent, false)
        return ItemsInDoorBinViewHolder(view, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    //Method to remove an item
    fun removeItem(item: ItemInDoorBinDataClass?){
        data.remove(item)
        removeItemFromDoorBinInBackend(item!!)
        notifyItemRemoved(data.indexOf(item))
        onDataSetChanged(data.isNotEmpty())
    }

    // Method to add an item
    fun addItems(items: MutableList<ItemInDoorBinDataClass>?) {
        data = items!!
        notifyItemInserted(data.size)
        onDataSetChanged(true)
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
        holder.binToBeMovedTo.text = "Floor"
        holder.lotNumberTextView.text = item.lotNumber
        holder.expirationDateTextView.text = item.expirationDate
        holder.quantityInDoorBinTextView.text = "Added: " + item.quantityOfItemsAddedToDoorBin.toString()
        holder.removeItemButton.setOnClickListener{
            // Create popup window to ask whether user wants to delete item or not
            val popupWindow = PopupWindowUtils.createQuestionPopup(it.context, "Are you sure you want to delete item from the list?", "Delete Item")
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


class ItemsInDoorBinViewHolder(itemInDoorBinView: View, private val listener: itemInDoorBinClickListener):RecyclerView.ViewHolder(itemInDoorBinView), View.OnClickListener {
    val itemNumberTextView: TextView = itemInDoorBinView.findViewById(R.id.itemNumberText)
    val itemNameTextView: TextView = itemInDoorBinView.findViewById(R.id.productNameText)
    val binToBeMovedTo: TextView = itemInDoorBinView.findViewById(R.id.binNumberText)
    val lotNumberTextView: TextView = itemInDoorBinView.findViewById(R.id.lotNumberText)
    val expirationDateTextView: TextView = itemInDoorBinView.findViewById(R.id.expDateInfoText)
    val quantityInDoorBinTextView: TextView = itemInDoorBinView.findViewById(R.id.addedText)
    val removeItemButton: AppCompatButton = itemInDoorBinView.findViewById(R.id.trashcanIconButton)

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