package com.example.cdihandheldscannerviewactivity.Utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import com.example.cdihandheldscannerviewactivity.R
import com.google.android.material.transition.platform.MaterialFadeThrough

class PopupWindowUtils {
    companion object{
        fun showErrorPopup(context: Context, anchor: View, message: String){
            val layoutInflater = LayoutInflater.from(context)
            val popupContentView = layoutInflater.inflate(R.layout.error_popup, null)

            val popupWindow = PopupWindow(
                popupContentView,
                400,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
//            val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
//            fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
//                override fun onAnimationStart(animation: Animation?) {
//                    // Animation started
//                }
//
//                override fun onAnimationEnd(animation: Animation?) {
//                    // Animation ended, dismiss the popup window
//                    popupWindow.dismiss()
//                }
//
//                override fun onAnimationRepeat(animation: Animation?) {
//                    // Animation repeating
//                }
//            })

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
    }
}