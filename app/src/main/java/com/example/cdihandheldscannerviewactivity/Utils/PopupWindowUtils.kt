package com.example.cdihandheldscannerviewactivity.Utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import com.example.cdihandheldscannerviewactivity.R
import com.google.android.material.transition.platform.MaterialFadeThrough

class PopupWindowUtils {
    companion object{
        fun showErrorPopup(context: Context, anchor: View, message: String){
            val layoutInflater = LayoutInflater.from(context)
            val popupContentView = layoutInflater.inflate(R.layout.popup_error, null)

            val popupWindow = PopupWindow(
                popupContentView,
                400,
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



        fun showConfirmationPopup(context: Context, anchor: View, confirmationText: String, confirmEditTextHint: String, confirmButtonOnClickListener: OnClickListener){
            val layoutInflater = LayoutInflater.from(context)
            val popupContentView = layoutInflater.inflate(R.layout.popup_confirmation, null)

            val popupWindow = PopupWindow(
                    popupContentView,
                    400,
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

            val confirmButton: Button
            confirmButton = popupContentView.findViewById(R.id.confirmButton)
            confirmButton.setOnClickListener(confirmButtonOnClickListener)


            popupWindow.apply {
                isOutsideTouchable = false
                isFocusable = true
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                showAtLocation(anchor, Gravity.CENTER, 0, 0)  // Optionally specify a location for the pop-up window
            }

        }
    }
}