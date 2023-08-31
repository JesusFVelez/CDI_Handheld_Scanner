package com.example.cdihandheldscannerviewactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.cdihandheldscannerviewactivity.databinding.FragmentProductToBinBinding


class ProductToBinFragment : Fragment() {

    private lateinit var binding: FragmentProductToBinBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
// TODO (3) Anñadir funcionalidad que haga que el formato del editText del Bin Number siempre tenga los guiones entremedio (ver commentarios en Figma)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_to_bin, container, false)

        return binding.root
    }

}