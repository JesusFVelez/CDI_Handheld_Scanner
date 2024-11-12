package com.scannerapp.cdihandheldscannerviewactivity.BinsWIthProduct

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.BinInfo
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class BinsWithProductAdapter (): RecyclerView.Adapter<BinWithProductViewHolder>()  {

    var data = listOf<BinInfo>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinWithProductViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.bins_with_product_bin_list_view, parent, false)
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


class BinWithProductViewHolder(binView: View) : RecyclerView.ViewHolder(binView) {
    val expirationDateTextView: TextView = binView.findViewById(R.id.expDateTextValue)
    val binLocationTextView: TextView = binView.findViewById(R.id.binLocation)
    val qtyAvailableTextView: TextView = binView.findViewById(R.id.QtyAvailTextValue)
    val binTypeTextView: TextView = binView.findViewById(R.id.binTypeText)
    val qtyOnHandAndInPickingTextView: TextView = binView.findViewById(R.id.inPickingAndOnHandTextValue)

    fun bind(data: binsWithProductViewData) {
        expirationDateTextView.text = when {
            data.ExpirationDate.equals("null", ignoreCase = true) || data.ExpirationDate.isEmpty() -> "Unavailable"
            else -> {
                // Attempt to parse and format the date
                try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
                    val date = inputFormat.parse(data.ExpirationDate)
                    if (date != null) {
                        outputFormat.format(date)
                    } else {
                        data.ExpirationDate // If parsing fails, display the original string
                    }
                } catch (e: ParseException) {
                    data.ExpirationDate // If parsing fails, display the original string
                }
            }
        }

        binLocationTextView.text = data.binLocation
        qtyAvailableTextView.text = data.QtyAvailable.toString()

        binTypeTextView.text = when (data.binType) {
            "R" -> "Bin Type: Reserve Bin"
            "" -> "Bin Type: Picking Bin"
            "X" -> "Bin Type: Virtual Bin"
            else -> "Bin Type: "
        }
        qtyOnHandAndInPickingTextView.text = "${data.QtyInPicking} / ${data.QtyOnHand}"
    }
}


