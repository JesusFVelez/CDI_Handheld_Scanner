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
import androidx.lifecycle.ViewModelProvider
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

    private lateinit var viewModel: AssignExpirationDateViewModel


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

        viewModel = ViewModelProvider(requireActivity())[AssignExpirationDateViewModel::class.java]

        setupUI()
        observeViewModel()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
            binding.upperDiv.visibility = View.GONE

    }

    private fun setupUI() {
        binding.enterButton.setOnClickListener {
            // Extract string values from EditText fields correctly
            val itemNumber = binding.itemNumberEditText.text.toString()
            val newExpirationDate = binding.NewExpirationDateEditText.text.toString()
            val binNumber = binding.BinNumberEditText.text.toString()

            // Use extracted String values for checks and ViewModel operations
            if (itemNumber.isNotBlank() && newExpirationDate.isNotBlank() && binNumber.isNotBlank()) {
                viewModel.assignExpirationDate(itemNumber, binNumber, newExpirationDate)
                viewModel.getItemInfo(itemNumber, binNumber)
            } else {
                Toast.makeText(context, "Make sure everything is filled", Toast.LENGTH_SHORT).show()
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
            }
        }

        viewModel.opMessage.observe(viewLifecycleOwner){ message ->
            if (message.isNotBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.itemInfo.observe(viewLifecycleOwner) { items ->
            // Check if the list is not empty
            if (items.isNotEmpty()) {

                val firstItem = items.first()
                binding.apply {
                    itemNumberTextView.text = firstItem.itemNumber
                    itemNameTextView.text = firstItem.itemDescription
                    expirationDateTextView.text = firstItem.expireDate
                    binLocationTextView.text = firstItem.binLocation

                    //To make upperDiv visible
                    upperDiv.visibility = View.VISIBLE
                }
            } else{
                binding.apply {
                    upperDiv.visibility = View.GONE
                }
            }
        }
    }
}

