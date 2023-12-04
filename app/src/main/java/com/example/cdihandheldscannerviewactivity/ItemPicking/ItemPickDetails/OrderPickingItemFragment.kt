package com.example.cdihandheldscannerviewactivity.ItemPicking.ItemPickDetails
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cdihandheldscannerviewactivity.ItemPicking.ItemPickingViewModel
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.databinding.FragmentOrderPickingItemBinding

class OrderPickingItemFragment :Fragment(){
    private lateinit var binding: FragmentOrderPickingItemBinding
    private val viewModel: ItemPickingViewModel by activityViewModels()

    // View Variable declarations
    private lateinit var itemNumberEditText: EditText
    private lateinit var pickItemButton:Button
    private lateinit var itemAmmountEditText: EditText
    private lateinit var itemNameTextView: TextView
    private lateinit var itemNumberTextView: TextView
    private lateinit var binNumberTextView: TextView
    private lateinit var totalQuantityToBePickedTextView: TextView



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_picking_item,container, false)
        initUIElements()

        return binding.root
    }

    private fun initUIElements(){
        itemNumberEditText = binding.itemNumberEditText
        pickItemButton = binding.pickingButton
        pickItemButton.setOnClickListener{

        }
        itemAmmountEditText = binding.itemAmountEditText
        itemNameTextView = binding.ItemName
        itemNumberTextView = binding.itemNumber
        binNumberTextView = binding.binLocationText
        totalQuantityToBePickedTextView = binding.totalQuantityOfItemsToBePickedText


    }


    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}