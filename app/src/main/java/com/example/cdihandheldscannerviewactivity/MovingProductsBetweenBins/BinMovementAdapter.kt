package com.example.cdihandheldscannerviewactivity.MovingProductsBetweenBins


import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cdihandheldscannerviewactivity.BinsWIthProduct.binsWithProductViewData
import com.example.cdihandheldscannerviewactivity.Utils.Network.BinInfo
import com.example.cdihandheldscannerviewactivity.R



abstract class BinsMovementAdapter : RecyclerView.Adapter<BinMovementViewHolder>()  {

    var data = listOf<BinInfo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size

    class BinMovementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binLocationTextView: TextView = view.findViewById(R.id.binLocation)
        private val binTypeTextView: TextView = view.findViewById(R.id.binTypeText)
        private val qtyAvailableTextView: TextView = view.findViewById(R.id.QtyAvailTextValue)
        private val qtyOnHandInBinTextView: TextView = view.findViewById(R.id.QtyOnHandText)

        fun bind(viewData: binsWithProductViewData) {
            binLocationTextView.text = viewData.binLocation
            binTypeTextView.text = viewData.binType
            qtyAvailableTextView.text = viewData.QtyAvailable.toString()
            qtyOnHandInBinTextView.text = viewData.QtyOnHand.toString()
        }
    }


}








