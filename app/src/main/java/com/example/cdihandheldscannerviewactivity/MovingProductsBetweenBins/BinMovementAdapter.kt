package com.example.cdihandheldscannerviewactivity.MovingProductsBetweenBins

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cdihandheldscannerviewactivity.R

class BinMovementAdapter : RecyclerView.Adapter<BinMovementViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinMovementViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: BinMovementViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}

data class BinMovementDataClass(
    val itemName: String,
    val itemNumber: String,
    val qtyToMoveFromBinToBin: Int,
    val fromBinNumber: String,
    val toBinNumber:String
)

class BinMovementViewHolder(itemToMoveView: View): RecyclerView.ViewHolder(itemToMoveView) {

    val itemNameTextView: TextView = itemToMoveView.findViewById(R.id.itemName)
    val itemNumberTextView: TextView = itemToMoveView.findViewById(R.id.itemNumber)
    val fromBinNumberTextView:TextView = itemToMoveView.findViewById(R.id.fromBinNumber)
    val toBinNumberTextView: TextView = itemToMoveView.findViewById(R.id.toBinNumber)
    val quantityToMoveTextView:TextView = itemToMoveView.findViewById(R.id.quantityToMove)
    val removeItemIconImageView: ImageView = itemToMoveView.findViewById(R.id.removeItemIcon)


    init {
//        itemToMoveView.setOnClickListener(this)

    }

    fun bind(data: BinMovementDataClass){
        itemNameTextView.text = data.itemName
        itemNumberTextView.text = data.itemNumber
        quantityToMoveTextView.text = data.qtyToMoveFromBinToBin.toString()
        fromBinNumberTextView.text = data.fromBinNumber
        toBinNumberTextView.text = data.toBinNumber
    }



}

interface removeItemTrashCanClickListener {
    fun onItemClickListener(view: View, position: Int)
}