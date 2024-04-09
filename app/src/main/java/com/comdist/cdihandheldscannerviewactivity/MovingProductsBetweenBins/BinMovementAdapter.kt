package com.comdist.cdihandheldscannerviewactivity.MovingProductsBetweenBins

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.comdist.cdihandheldscannerviewactivity.R

class BinMovementAdapter () : RecyclerView.Adapter<BinMovementViewHolder>(){

    var data = mutableListOf<BinMovementDataClass>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinMovementViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_to_move_view, parent, false)
        return BinMovementViewHolder(view)
    }

    // Method to add an item
    fun addItem(item: BinMovementDataClass?) {
        data.add(item!!)
        notifyItemInserted(data.size - 1)
    }


    //Method to remove an item
    private fun removeItem(item: BinMovementDataClass?){
        data.remove(item)
        notifyItemRemoved(data.indexOf(item))
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
    val qtyToMoveFromBinToBin: Int,
    val fromBinNumber: String,
    val toBinNumber:String
)

class BinMovementViewHolder(itemToMoveView: View): RecyclerView.ViewHolder(itemToMoveView){

    val itemNameTextView: TextView = itemToMoveView.findViewById(R.id.itemName)
    val itemNumberTextView: TextView = itemToMoveView.findViewById(R.id.itemNumber)
    val fromBinNumberTextView:TextView = itemToMoveView.findViewById(R.id.fromBinNumber)
    val toBinNumberTextView: TextView = itemToMoveView.findViewById(R.id.toBinNumber)
    val quantityToMoveTextView:TextView = itemToMoveView.findViewById(R.id.quantityToMove)
    val removeItemIconButton: Button = itemToMoveView.findViewById(R.id.removeItemIcon)


    fun bind(data: BinMovementDataClass, onRemoveOfItem:(Int) -> Unit){
        itemNameTextView.text = data.itemName
        itemNumberTextView.text = data.itemNumber
        quantityToMoveTextView.text = "Count: " + data.qtyToMoveFromBinToBin.toString()
        fromBinNumberTextView.text = data.fromBinNumber
        toBinNumberTextView.text = data.toBinNumber
        removeItemIconButton.setOnClickListener{
            onRemoveOfItem(adapterPosition)
        }

    }




}

