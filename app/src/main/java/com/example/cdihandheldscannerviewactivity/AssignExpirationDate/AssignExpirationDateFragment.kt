package com.example.cdihandheldscannerviewactivity.AssignExpirationDate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.databinding.FragmentAssignExpirationDateBinding


class AssignExpirationDateFragment : Fragment() {
    private lateinit var binding : FragmentAssignExpirationDateBinding

    private lateinit var itemNumberTextView: TextView
    private lateinit var itemNameTextView: TextView
    private lateinit var expirationDateTextView: TextView
    private lateinit var binLocationTextView:TextView
    private lateinit var BinNumberEditText: EditText
    private lateinit var itemNumberEditText: EditText
    private lateinit var NewExpirationDateEditText: EditText
    private lateinit var enterButton: Button

    override fun onCreate(saveInstance: Bundle?){
        super.onCreate(saveInstance)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_assign_expiration_date, container, false)

        initUIElements()

        return binding.root
    }

    private fun initUIElements(){

        itemNumberTextView = binding.itemNumberTextView
        itemNameTextView = binding.itemNameTextView
        expirationDateTextView = binding.expirationDateTextView
        binLocationTextView = binding.binLocationTextView
        BinNumberEditText = binding.BinNumberEditText
        itemNumberEditText = binding.itemNumberEditText
        NewExpirationDateEditText = binding.NewExpirationDateEditText
        enterButton = binding.enterButton


    }
}
