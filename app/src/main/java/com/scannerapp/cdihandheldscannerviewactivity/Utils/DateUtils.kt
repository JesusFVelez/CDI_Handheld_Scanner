package com.scannerapp.cdihandheldscannerviewactivity.Utils

class DateUtils {
    companion object {
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