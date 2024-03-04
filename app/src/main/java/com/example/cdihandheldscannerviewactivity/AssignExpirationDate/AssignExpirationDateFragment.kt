package com.example.cdihandheldscannerviewactivity.AssignExpirationDate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.example.cdihandheldscannerviewactivity.databinding.FragmentAssignExpirationDateBinding


class AssignExpirationDateFragment : Fragment() {
    private lateinit var binding: FragmentAssignExpirationDateBinding

    private lateinit var itemNumberTextView: TextView
    private lateinit var itemNameTextView: TextView
    private lateinit var expirationDateTextView: TextView
    private lateinit var binLocationTextView: TextView
    private lateinit var BinNumberEditText: EditText
    private lateinit var itemNumberEditText: EditText
    private lateinit var NewExpirationDateEditText: EditText
    private lateinit var enterButton: Button

    private val viewModel: AssignExpirationDateViewModel by activityViewModels()


    override fun onCreate(saveInstance: Bundle?) {
        super.onCreate(saveInstance)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_assign_expiration_date,
            container,
            false
        )

        setupUI()
        observeViewModel()

        return binding.root
    }

    private fun setupUI() {
        with(binding) {
            val itemNumber = itemNumberEditText.text.toString()
            val newExpirationDate = NewExpirationDateEditText.text.toString()
            val binNumber = BinNumberEditText.text.toString()
            if (itemNumber.isNotBlank() && newExpirationDate.isNotBlank() && binNumber.isNotBlank()) {
                viewModel.assignExpirationDate(itemNumber, newExpirationDate, binNumber)
            } else {
                Toast.makeText(context, "Make sure everything is filled", Toast.LENGTH_SHORT).show()
            }
            if (itemNumber.isNotBlank() && newExpirationDate.isNotBlank() && binNumber.isNotBlank()) {
                viewModel.getItemInfo(itemNumber, binNumber)
            }
        }


    }

    private fun observeViewModel() {
        // Observe the operation success LiveData
        viewModel.opSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(
                    context,
                    "Expiration date assigned successfully.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(context, "Failed to assign expiration date.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.itemInfo.observe(viewLifecycleOwner) { item ->
            if (item != null) {
                binding.apply {
                    itemNumberTextView.text = item.itemNumber
                    itemNameTextView.text = item.itemDescription
                    expirationDateTextView.text = item.expireDate
                    binLocationTextView.text = item.binLocation
                }
            }
        }
    }
}

