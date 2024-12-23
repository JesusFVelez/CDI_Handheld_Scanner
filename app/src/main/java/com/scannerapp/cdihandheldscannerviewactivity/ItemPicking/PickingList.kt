package com.scannerapp.cdihandheldscannerviewactivity.ItemPicking

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.PopupWindow
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemsInOrderInfo
import com.scannerapp.cdihandheldscannerviewactivity.Utils.PopupWindowUtils

class PickingList() {
    private lateinit var listOfItemsToPick: List<ItemsInOrderInfo>
    private var currentItemIndex: Int
    private var currentPopupWindow: PopupWindow?
    private var totalNumOfItemsToPick: Int

    init {
        listOfItemsToPick = listOf()
        currentPopupWindow = null
        currentItemIndex = -1
        totalNumOfItemsToPick = 0
    }

    constructor(newListOfItems: List<ItemsInOrderInfo>) : this() {
        listOfItemsToPick = newListOfItems
        currentItemIndex = -1
        totalNumOfItemsToPick = newListOfItems.size
        currentPopupWindow = null
    }

    constructor(newListOfItems: List<ItemsInOrderInfo>, indexToStartIn: Int) : this() {
        listOfItemsToPick = newListOfItems
        totalNumOfItemsToPick = newListOfItems.size
        setCurrentItemIndex(indexToStartIn)
//        moveToNextItemNotPicked()
        currentPopupWindow = null
    }

    fun setListOfItemsToPick(listOfItemsInOrder: List<ItemsInOrderInfo>){
        if(listOfItemsInOrder.isNotEmpty()) {
            listOfItemsToPick = listOfItemsInOrder
            totalNumOfItemsToPick = listOfItemsToPick.size
        }

    }

    fun getListOfItemsToPick(): List<ItemsInOrderInfo>{
        return listOfItemsToPick
    }

    fun getCurrentItem(): ItemsInOrderInfo{
        return listOfItemsToPick[currentItemIndex]
    }

    fun getNumOfItemsToPick(): Int{
        return totalNumOfItemsToPick
    }

    fun moveToNextItemNotPicked(){
        var nextIndex = currentItemIndex + 1
//        currentItemIndex = if(nextIndex >= totalNumOfItemsToPick) nextIndex % totalNumOfItemsToPick else nextIndex
        if(nextIndex >= totalNumOfItemsToPick)
            nextIndex = 0
        for(index in nextIndex until totalNumOfItemsToPick){
            if(listOfItemsToPick[index].itemPickingStatus == "Not Picked") {
                currentItemIndex = index
                return
            }
        }
        currentItemIndex = 0
    }

    fun getCurrentIndex(): Int{
        return currentItemIndex
    }


    fun setCurrentItemIndex(index: Int){
        currentItemIndex = if(index >= 0) index else if(index >= totalNumOfItemsToPick) index % totalNumOfItemsToPick else 0
    }



    private fun initializeNewPopupWindow(context: Context, view: View, listener:PopupWindowUtils.Companion.PopupInputListener): PopupWindow{
        return PopupWindowUtils.showConfirmationPopup(context, view, "Scan Bin '" + listOfItemsToPick[currentItemIndex].binLocation + "' to continue",  "Bin Number", listener, hasNextButton = true)
    }


    fun startPickingLoop(context: Context, viewModel: ItemPickingViewModel, view: View) {
        val listener = object : PopupWindowUtils.Companion.PopupInputListener {
            override fun onConfirm(input: EditText) {
                viewModel.confirmBin(input.text.toString())
            }

            override fun onNextPress() {
//                currentItemIndex+= 1
//                if(currentItemIndex >= totalNumOfItemsToPick)
//                    currentItemIndex %= totalNumOfItemsToPick
                moveToNextItemNotPicked()

                if(currentPopupWindow != null){
                    currentPopupWindow!!.dismiss() // Dismisses the current one and moves on to the next popup window for the next item
                    currentPopupWindow = initializeNewPopupWindow(context, view, this)
                }
            }
        }

        currentPopupWindow = initializeNewPopupWindow(context, view, listener)
    }

}