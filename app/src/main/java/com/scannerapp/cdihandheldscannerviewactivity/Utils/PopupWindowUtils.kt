package com.scannerapp.cdihandheldscannerviewactivity.Utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.google.android.material.transition.platform.MaterialFadeThrough

class PopupWindowUtils {
    companion object{


        fun getLoadingPopup(context: Context):Dialog{
            return Dialog(context).apply{
                setContentView(R.layout.dialog_loading)
                setCancelable(false)
                // Make the dialog background transparent to show the rounded corners
                window?.setBackgroundDrawableResource(android.R.color.transparent)
            }
        }

        interface PopupInputListener{
            fun onConfirm(input: EditText)
            fun onNextPress(): Boolean {
                // Does nothing by default
                // This is because I want to make this interface re-usable for all the popups that have a next text on it
                return false
            }
        }




        fun showErrorPopup(context: Context, anchor: View, message: String){
            val layoutInflater = LayoutInflater.from(context)
            val popupContentView = layoutInflater.inflate(R.layout.popup_error, null)

            val popupWidth = getPopupWindowWidth(context)
            val popupWindow = PopupWindow(
                popupContentView,
                popupWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            popupWindow.enterTransition = MaterialFadeThrough().apply { // this worked
                duration = 400
            }
            popupWindow.exitTransition = MaterialFadeThrough().apply { // this worked
                duration = 400
            }


            val textDescription = popupContentView.findViewById<TextView>(R.id.errorDescription)
            textDescription.text =  message
            val closeButton = popupContentView.findViewById<Button>(R.id.okButton)
            closeButton.setOnClickListener {
//                popupContentView.startAnimation(fadeOutAnimation)
                popupWindow.dismiss()
            }
            popupWindow.isOutsideTouchable = false
            popupWindow.isFocusable = true
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


            // Optionally specify a location for the pop-up window
            popupWindow.showAtLocation(anchor, Gravity.CENTER, 0, 0)
        }


        fun createQuestionPopup(context: Context, questionDescription: String, questionTitle:String ): PopupWindow{
            val layoutInflater = LayoutInflater.from(context)
            val popupContentView = layoutInflater.inflate(R.layout.popup_question, null)

            val popupWidth = getPopupWindowWidth(context)
            val popupWindow = PopupWindow(
                popupContentView,
                popupWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            popupWindow.enterTransition = MaterialFadeThrough().apply { // this worked
                duration = 400
            }
            popupWindow.exitTransition = MaterialFadeThrough().apply { // this worked
                duration = 400
            }


            val textDescription = popupContentView.findViewById<TextView>(R.id.questionDescription)
            textDescription.text =  questionDescription

            val titleText = popupContentView.findViewById<TextView>(R.id.questionText)
            titleText.text = questionTitle

            popupWindow.isOutsideTouchable = false
            popupWindow.isFocusable = true
            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


            // Optionally specify a location for the pop-up window
//            popupWindow.showAtLocation(anchor, Gravity.CENTER, 0, 0)

            return popupWindow
        }


        private fun getPopupWindowWidth(context: Context): Int {
            // Get screen width
            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            return screenWidth - (screenWidth * 0.05).toInt()
        }


        fun getCustomPopup(context: Context, customPopupXML: Int): PopupWindow{
            val layoutInflater = LayoutInflater.from(context)
            val popupContentView = layoutInflater.inflate(customPopupXML, null)


            val popupWidth = getPopupWindowWidth(context)
            val popupWindow = PopupWindow(
                popupContentView,
                popupWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            popupWindow.enterTransition = MaterialFadeThrough().apply { // this worked
                duration = 400
            }
            popupWindow.exitTransition = MaterialFadeThrough().apply { // this worked
                duration = 400
            }



            popupWindow.apply {
                isOutsideTouchable = false
                isFocusable = true
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            return popupWindow

        }


        fun showConfirmationPopup(context: Context, anchor: View, confirmationText: String, confirmEditTextHint: String, listener: PopupInputListener, inlineErrorText: String = "Field left empty, please enter a value"):PopupWindow {
            val layoutInflater = LayoutInflater.from(context)

            val popupContentView = if(!listener.onNextPress())
                layoutInflater.inflate(R.layout.popup_confirmation, null)
            else
                layoutInflater.inflate(R.layout.popup_confirmation_with_next, null)


            val popupWidth = getPopupWindowWidth(context)
            val popupWindow = PopupWindow(
                    popupContentView,
                    popupWidth,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )

            popupWindow.enterTransition = MaterialFadeThrough().apply { // this worked
                duration = 400
            }
            popupWindow.exitTransition = MaterialFadeThrough().apply { // this worked
                duration = 400
            }

            val confirmationDescriptionText: TextView
            confirmationDescriptionText = popupContentView.findViewById(R.id.confirmationDescription)
            confirmationDescriptionText.text = confirmationText

            val confirmEditText: EditText
            confirmEditText = popupContentView.findViewById(R.id.confirmationEditText)
            confirmEditText.hint = confirmEditTextHint
            confirmEditText.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                    // Handle the Enter key press here
                    if(confirmEditText.text.toString().isNotEmpty()){
                        listener.onConfirm(confirmEditText)
                        popupWindow.dismiss()
                        true
                    }else{
                        confirmEditText.error = inlineErrorText
                        false
                    }
                } else {
                    false
                }
            }

            val confirmButton: Button
            confirmButton = popupContentView.findViewById(R.id.confirmButton)
            confirmButton.setOnClickListener{
                if(confirmEditText.text.toString().isNotEmpty()) {
                        listener.onConfirm(confirmEditText)
                        popupWindow.dismiss()
                }else
                    confirmEditText.error = inlineErrorText
            }

            if(listener.onNextPress()){
                val nextButton: Button = popupContentView.findViewById(R.id.nextItemImageButton)
                nextButton.setOnClickListener {
                    listener.onNextPress()
                }
            }




            popupWindow.apply {
                isOutsideTouchable = false
                isFocusable = true
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                showAtLocation(anchor, Gravity.CENTER, 0, 0)  // Optionally specify a location for the pop-up window
            }

            return popupWindow
        }
    }
}