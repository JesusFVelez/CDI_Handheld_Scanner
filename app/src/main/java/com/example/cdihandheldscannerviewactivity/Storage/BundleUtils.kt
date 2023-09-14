package com.example.cdihandheldscannerviewactivity.Storage

import android.os.Bundle

class BundleUtils {
    companion object{
        private const val sourceFragmentKey = "source_fragment"
        fun getBundleToSendFragmentNameToNextFragment(sourceFragmentName: String): Bundle {
            val bundle = Bundle()
            bundle.putString(sourceFragmentKey, sourceFragmentName)
            return bundle
        }

        fun getPastFragmentNameFromBundle(bundle : Bundle?): String{
            return if(bundle != null) {
                var sourceFragment = bundle.getString(sourceFragmentKey).toString()
                if (sourceFragment == null)
                    sourceFragment = "N/A"
                sourceFragment
            } else
                "N/A"
        }
    }
}