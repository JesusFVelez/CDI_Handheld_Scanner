package com.example.cdihandheldscannerviewactivity.MovingProductsBetweenBins

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cdihandheldscannerviewactivity.ProductsInBin.productInBinViewHolder
import com.example.cdihandheldscannerviewactivity.R

class BinMovementAdapter (private val trashCanOnClickListener: OnClickListener) : RecyclerView.Adapter<BinMovementViewHolder>(){

    var data = listOf<BinMovementDataClass>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinMovementViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_to_move_view, parent, false)
        return BinMovementViewHolder(view, trashCanOnClickListener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BinMovementViewHolder, position: Int) {
        holder.bind(data[position])
    }
}

data class BinMovementDataClass(
    val itemName: String,
    val itemNumber: String,
    val qtyToMoveFromBinToBin: Int,
    val fromBinNumber: String,
    val toBinNumber:String
)

class BinMovementViewHolder(itemToMoveView: View, removeIconClickListener: OnClickListener): RecyclerView.ViewHolder(itemToMoveView){

    val itemNameTextView: TextView = itemToMoveView.findViewById(R.id.itemName)
    val itemNumberTextView: TextView = itemToMoveView.findViewById(R.id.itemNumber)
    val fromBinNumberTextView:TextView = itemToMoveView.findViewById(R.id.fromBinNumber)
    val toBinNumberTextView: TextView = itemToMoveView.findViewById(R.id.toBinNumber)
    val quantityToMoveTextView:TextView = itemToMoveView.findViewById(R.id.quantityToMove)
    val removeItemIconButton: Button = itemToMoveView.findViewById(R.id.removeItemIcon)


    init {
//        itemToMoveView.setOnClickListener(this)
        removeItemIconButton.setOnClickListener(removeIconClickListener)
    }

    fun bind(data: BinMovementDataClass){
        itemNameTextView.text = data.itemName
        itemNumberTextView.text = data.itemNumber
        quantityToMoveTextView.text = "Count: " + data.qtyToMoveFromBinToBin.toString()
        fromBinNumberTextView.text = data.fromBinNumber
        toBinNumberTextView.text = data.toBinNumber
    }



}

