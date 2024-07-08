package com.scannerapp.cdihandheldscannerviewactivity.EditItem.EditBarcode

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.PopupWindowUtils

class EditItemBarcodesAdapter(private val listener: BarcodeClickListener, private val deleteBarcodeEvent: (barcodeToRemove: String) -> Unit) :
    RecyclerView.Adapter<EditItemBarcodeViewHolder>() {

    private var _data = mutableListOf<String>()
    var data: List<String>
        get() = _data
        set(value) {
            _data = value.toMutableList()
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditItemBarcodeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(
            R.layout.edit_item_barcode_to_edit_list_view,
            parent,
            false
        )
        return EditItemBarcodeViewHolder(view, listener)
    }

    fun addBarcode(barcode: String) {
        _data.add(barcode)
        notifyItemInserted(_data.size - 1)
    }

    fun removeItem(barcode: String) {
        val index = _data.indexOf(barcode)
        if (index != -1) {
            _data.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun getItemCount(): Int = _data.size

    override fun onBindViewHolder(holder: EditItemBarcodeViewHolder, position: Int) {
        holder.bind(_data[position]) {
            deleteBarcodeEvent(_data[position])
        }
    }
}

class EditItemBarcodeViewHolder(
    barcodeView: View,
    private val listener: BarcodeClickListener
) : RecyclerView.ViewHolder(barcodeView), View.OnClickListener {

    private val itemBarcode: TextView = barcodeView.findViewById(R.id.barcodeTextView)
    private val removeBarcodeIcon: ImageButton =
        barcodeView.findViewById(R.id.removeBarcodeIcon)

    init {
        barcodeView.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        listener.onBarcodeClick(view!!, adapterPosition)
    }

    fun bind(barcode: String, onRemove: () -> Unit) {
        itemBarcode.text = barcode
        removeBarcodeIcon.setOnClickListener {
            // Create popup window to ask whether user wants to delete barcode or not
            val popupWindow = PopupWindowUtils.createQuestionPopup(
                it.context,
                "Are you sure you want to delete this barcode for this item?",
                "Delete Barcode"
            )
            popupWindow.contentView.findViewById<Button>(R.id.YesButton)
                .setOnClickListener {
                    popupWindow.dismiss()
                    onRemove()
                }
            popupWindow.contentView.findViewById<Button>(R.id.NoButton)
                .setOnClickListener {
                    popupWindow.dismiss()
                }
            popupWindow.showAtLocation(it.rootView, Gravity.CENTER, 0, 0)
        }
    }
}

fun interface BarcodeClickListener {
    fun onBarcodeClick(view: View, position: Int)
}