package com.comdist.cdihandheldscannerviewactivity.MovingProductsBetweenBins

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.PopupWindowUtils

class BinMovementAdapter (private val onDataSetChanged: (Boolean) -> Unit, private val listener: ItemToMoveOnClickListener) : RecyclerView.Adapter<BinMovementViewHolder>(){

    var data = mutableListOf<BinMovementDataClass>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinMovementViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.bin_movement_item_to_move_view, parent, false)
        return BinMovementViewHolder(view, listener)
    }

    // Method to add an item
    fun addItem(item: BinMovementDataClass?) {
        data.add(item!!)
        notifyItemInserted(data.size - 1)
        onDataSetChanged(true)
    }

    fun updateItem(itemPosition:Int ,item: BinMovementDataClass?){
        if(itemPosition >= 0 && itemPosition < data.size) {
            data.removeAt(itemPosition)
            data.add(item!!)
            notifyDataSetChanged()
            onDataSetChanged(true)
        }else
            addItem(item)
    }

    fun clearAllItems(){
        data.clear()
        notifyDataSetChanged()
        onDataSetChanged(data.isNotEmpty())
    }


    //Method to remove an item
    fun removeItem(item: BinMovementDataClass?){
        data.remove(item)
        notifyDataSetChanged()
        onDataSetChanged(data.isNotEmpty())
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BinMovementViewHolder, position: Int) {
        holder.bind(data[position], {removeItem(data[position])})
    }


}

data class BinMovementDataClass(
    val itemName: String,
    val itemNumber: String,
    val rowIDOfItemInFromBin: String,
    val qtyToMoveFromBinToBin: Int,
    val fromBinNumber: String,
    val toBinNumber:String
)

class BinMovementViewHolder(itemToMoveView: View, private val listener: ItemToMoveOnClickListener): RecyclerView.ViewHolder(itemToMoveView), View.OnClickListener{

    val itemNameTextView: TextView = itemToMoveView.findViewById(R.id.itemName)
    val itemNumberTextView: TextView = itemToMoveView.findViewById(R.id.itemNumber)
    val fromBinNumberTextView:TextView = itemToMoveView.findViewById(R.id.fromBinNumber)
    val toBinNumberTextView: TextView = itemToMoveView.findViewById(R.id.toBinNumber)
    val quantityToMoveTextView:TextView = itemToMoveView.findViewById(R.id.quantityToMove)
    val removeItemIconButton: Button = itemToMoveView.findViewById(R.id.removeItemIcon)

    init {
        itemToMoveView.setOnClickListener(this)
    }

    fun bind(data: BinMovementDataClass, onRemoveOfItem:(Int) -> Unit){
        itemNameTextView.text = data.itemName
        itemNumberTextView.text = data.itemNumber
        quantityToMoveTextView.text = "Count: " + data.qtyToMoveFromBinToBin.toString()
        fromBinNumberTextView.text = data.fromBinNumber
        if(data.toBinNumber.isEmpty())
            toBinNumberTextView.text = "Not Chosen"
        else
            toBinNumberTextView.text = data.toBinNumber

        removeItemIconButton.setOnClickListener{

            // Create popup window to ask whether user wants to delete item or not
            val popupWindow = PopupWindowUtils.createQuestionPopup(it.context, "Are you sure you want to delete item from the list?", "Delete Item")
            popupWindow.contentView.findViewById<Button>(R.id.YesButton).setOnClickListener {
                popupWindow.dismiss()
                onRemoveOfItem(adapterPosition)
            }
            popupWindow.contentView.findViewById<Button>(R.id.NoButton).setOnClickListener {
                popupWindow.dismiss()
            }
            popupWindow.showAtLocation(it.rootView, Gravity.CENTER, 0, 0)

        }

    }

    override fun onClick(view: View?) {
        listener.onItemClickListener(view!!, adapterPosition)
    }

}

fun interface ItemToMoveOnClickListener{
    fun onItemClickListener(view: View, position: Int)
}

