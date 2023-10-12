package com.example.cdihandheldscannerviewactivity.BinsWIthProduct

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cdihandheldscannerviewactivity.Network.BinInfo
import com.example.cdihandheldscannerviewactivity.R

class BinsWithProductAdapter (): RecyclerView.Adapter<BinWithProductViewHolder>()  {

    var data = listOf<BinInfo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinWithProductViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.bins_that_have_product_view, parent, false)
        return BinWithProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BinWithProductViewHolder, position: Int) {
        val bin = data[position]
        var viewData = binsWithProductViewData(bin.binLocation, bin.binType, bin.qtyAvailable.toInt(), bin.qtyOnHandInBin.toInt(), bin.qtyInPickingInBin.toInt(), bin.expirationDateOfItemInBin.toString())
        holder.bind(viewData)
    }

}






data class binsWithProductViewData(
    val binLocation: String,
    val binType: String,
    val QtyAvailable: Int,
    val QtyOnHand: Int,
    val QtyInPicking: Int,
    val ExpirationDate: String
)






class BinWithProductViewHolder (binView: View): RecyclerView.ViewHolder(binView){
    val expirationDateTextView: TextView = binView.findViewById(R.id.expDateTextValue)
    val binLocationTextView: TextView = binView.findViewById(R.id.binLocation)
    val qtyAvailableTextView: TextView = binView.findViewById(R.id.QtyAvailTextValue)
    val binTypeTextView: TextView = binView.findViewById(R.id.binTypeText)
    val qtyOnHandAndInPickingTextView: TextView = binView.findViewById(R.id.inPickingAndOnHandTextValue)

    init {
    }

    fun bind(data: binsWithProductViewData){
        expirationDateTextView.text = when(data.ExpirationDate) {
            "null" -> "Unavailable"
            else -> data.ExpirationDate
        }
        binLocationTextView.text = data.binLocation
        qtyAvailableTextView.text = data.QtyAvailable.toString()

        binTypeTextView.text = when(data.binType) {
            "R" -> "Bin Type: Reserve Bin"
            ""  -> "Bin Type: Picking Bin"
            "X" -> "Bin Type: Virtual Bin"
            else -> "Bin Type: "
        }
        qtyOnHandAndInPickingTextView.text = "${data.QtyOnHand} / ${data.QtyInPicking}"
    }



}

