package com.scannerapp.cdihandheldscannerviewactivity.Utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import com.scannerapp.cdihandheldscannerviewactivity.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateUtils {
    companion object {

        fun isDateExpired(dateStr: String, handleYesPressed: () -> Unit, context: Context, view: View): Boolean{
            var isDateExpired: Boolean
            try {
                val date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("MM-dd-yyyy"))
                isDateExpired = date.isBefore(LocalDate.now())

            }catch (e: Exception) {
                isDateExpired = false
            }
            if(isDateExpired){
                val popup = PopupWindowUtils.createQuestionPopup(context, "The date ${dateStr} has already expired, \n would you like to use this date anyway?", "Date expired")
                val yesOnClickListener = View.OnClickListener{
                    handleYesPressed()
                    popup.dismiss()
                }
                popup.contentView.findViewById<Button>(R.id.YesButton).setOnClickListener(yesOnClickListener)
                popup.contentView.findViewById<Button>(R.id.NoButton).setOnClickListener{
                    popup.dismiss()
                }
                popup.showAtLocation(view, Gravity.CENTER, 0, 0)
            }
            return isDateExpired
        }

        // Function to validate the date
        fun isValidDate(dateStr: String): Boolean {
            val parts = dateStr.split("-")
            if (parts.size == 3) {
                val month = parts[0]
                val day = parts[1]
                val year = parts[2]

                // Check if each part of the date is a valid number
                if (month.length != 2 || !month.all { it.isDigit() } ||
                    day.length != 2 || !day.all { it.isDigit() } ||
                    (year.length != 2 && year.length != 4) || !year.all { it.isDigit() }) {
                    return false
                }

                val monthInt = month.toInt()
                val dayInt = day.toInt()
                val yearInt = year.toInt()


                if (monthInt !in 1..12) return false

                val maxDays = when (monthInt) {
                    1, 3, 5, 7, 8, 10, 12 -> 31
                    4, 6, 9, 11 -> 30
                    2 -> if (yearInt % 4 == 0 && (yearInt % 100 != 0 || yearInt % 400 == 0)) 29 else 28
                    else -> return false
                }

                if (dayInt !in 1..maxDays) return false
            } else {
                return false
            }

            return true
        }
    }

}