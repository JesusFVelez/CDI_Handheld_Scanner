package com.scannerapp.cdihandheldscannerviewactivity.ItemPicking

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.PopupWindow
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemsInOrderInfo
import com.scannerapp.cdihandheldscannerviewactivity.Utils.PopupWindowUtils

class PickingList {
    private lateinit var pickingList: List<ItemsInOrderInfo>
    private var currentItemIndex: Int
    private var currentPopupWindow: PopupWindow?

    constructor(){
        pickingList = listOf()
        currentPopupWindow = null
        currentItemIndex = -1
    }

    fun setPickingList(listOfItemsInOrder: List<ItemsInOrderInfo>){
        if(listOfItemsInOrder.isNotEmpty()) {
            pickingList = listOfItemsInOrder
            currentItemIndex = 0
        }

    }

    fun getCurrentItem(): ItemsInOrderInfo{
        return pickingList[currentItemIndex]
    }

    private fun initializeNewPopupWindow(context: Context, view: View, listener:PopupWindowUtils.Companion.PopupInputListener): PopupWindow{
        return PopupWindowUtils.showConfirmationPopup(context, view, "Scan Bin '" + pickingList[currentItemIndex].binLocation + "' to continue", "Bin Number", listener)
    }


    fun startPickingLoop(context: Context, /*onOkClickListener: PopupWindowUtils.Companion.PopupInputListener,*/ viewModel: ItemPickingViewModel, view: View) {
        val listener = object : PopupWindowUtils.Companion.PopupInputListener {
            override fun onConfirm(input: EditText) {
                viewModel.setChosenAdapterPosition(currentItemIndex)
                viewModel.setCurrentlyChosenItem()
                viewModel.confirmBin(input.text.toString(), currentItemIndex)
                currentItemIndex+= 1
            }

            override fun onNextPress(): Boolean {
                currentItemIndex+= 1
                if(currentPopupWindow != null)
                    currentPopupWindow!!.dismiss() // Dismisses the current one and moves on to the next popup window for the next item
                currentPopupWindow = initializeNewPopupWindow(context, view, this)
                return true
            }
        }
        currentPopupWindow = initializeNewPopupWindow(context, view, listener)
    }

}