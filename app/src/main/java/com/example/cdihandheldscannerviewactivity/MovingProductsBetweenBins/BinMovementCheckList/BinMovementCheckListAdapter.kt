package com.example.cdihandheldscannerviewactivity.MovingProductsBetweenBins.BinMovementCheckList

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cdihandheldscannerviewactivity.R

class BinMovementCheckListAdapter (private val checkListOnClickListener: OnClickListener) : RecyclerView.Adapter<BinMovementCheckListViewHolder>(){
    var data = listOf<BinMovementCheckListDataClass>()
        set(value){
           field = value
           notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinMovementCheckListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_to_move_check_list_view, parent, false)
        return BinMovementCheckListViewHolder(view, checkListOnClickListener)
    }

    override fun onBindViewHolder(holder: BinMovementCheckListViewHolder, position: Int) {
        holder.bind(data[position])
    }

}

data class BinMovementCheckListDataClass(
    val itemName: String,
    val itemNumber: String,
    val qtyToMoveFromBinToBin: Int,
    val fromBinNumber: String,
    val toBinNumber:String
)

class BinMovementCheckListViewHolder(itemToMoveCheckListView: View, checkMarkListIconListener: OnClickListener): RecyclerView.ViewHolder(itemToMoveCheckListView){
    val itemNameTextView: TextView = itemToMoveCheckListView.findViewById(R.id.itemName)
    val itemNumberTextView: TextView = itemToMoveCheckListView.findViewById(R.id.itemNumber)
    val fromBinNumberTextView:TextView = itemToMoveCheckListView.findViewById(R.id.fromBinNumber)
    val toBinNumberTextView: TextView = itemToMoveCheckListView.findViewById(R.id.toBinNumber)
    val quantityToMoveTextView:TextView = itemToMoveCheckListView.findViewById(R.id.quantityToMove)
    val checkMarkListIcon: Button = itemToMoveCheckListView.findViewById(R.id.checkMarkListIcon)
init{
    checkMarkListIcon.setOnClickListener(checkMarkListIconListener)
}

    fun bind(data: BinMovementCheckListDataClass){
        itemNameTextView.text = data.itemName
        itemNumberTextView.text = data.itemNumber
        quantityToMoveTextView.text = "Count: " + data.qtyToMoveFromBinToBin.toString()
        fromBinNumberTextView.text = data.fromBinNumber
        toBinNumberTextView.text = data.toBinNumber
    }

}