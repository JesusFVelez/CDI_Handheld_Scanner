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
import com.example.cdihandheldscannerviewactivity.login.LoginViewModel


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

    private val viewModel: AssignExpirationDateViewModel by activityViewModels()


    override fun onCreate(saveInstance: Bundle?){
        super.onCreate(saveInstance)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_assign_expiration_date, container, false)

        setupUI()
        observeViewModel()

        return binding.root
    }

    private fun setupUI() {
        binding.enterButton.setOnClickListener {
            val itemNumber = binding.itemNumberEditText.text.toString()
            val binNumber = binding.BinNumberEditText.text.toString()
            val newExpirationDate = binding.NewExpirationDateEditText.text.toString()
            if(binNumber.isNotEmpty()){

            }

            // Call the ViewModel function to assign expiration date
            viewModel.assignExpirationDate(itemNumber, binNumber, newExpirationDate)
        }
    }

    private fun observeViewModel() {
        // Observe the operation success LiveData
        viewModel.opSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Expiration date assigned successfully.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to assign expiration date.", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe the operation message LiveData
        viewModel.opMessage.observe(viewLifecycleOwner) { message ->
            AlerterUtils.startSuccessAlert(requireActivity(),"Success",message)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}

