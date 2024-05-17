package com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage

import android.os.Bundle

// Class for handling Bundle related utilities
class BundleUtils {
    companion object{
        // The key used for storing and retrieving the source fragment name in the Bundle
        private const val sourceFragmentKey = "source_fragment"

        // Method for creating a Bundle to send the source fragment name to the next fragment
        fun getBundleToSendFragmentNameToNextFragment(sourceFragmentName: String): Bundle {
            val bundle = Bundle()
            // Put the source fragment name into the Bundle
            bundle.putString(sourceFragmentKey, sourceFragmentName)
            return bundle
        }

        // Method for retrieving the source fragment name from a Bundle
        fun getPastFragmentNameFromBundle(bundle : Bundle?): String{
            return if(bundle != null) {
                // Get the source fragment name from the Bundle, default to "N/A" if it's not present
                var sourceFragment = bundle.getString(sourceFragmentKey).toString()
                if (sourceFragment == null)
                    sourceFragment = "N/A"
                sourceFragment
            } else
                "N/A"
        }
    }
}
