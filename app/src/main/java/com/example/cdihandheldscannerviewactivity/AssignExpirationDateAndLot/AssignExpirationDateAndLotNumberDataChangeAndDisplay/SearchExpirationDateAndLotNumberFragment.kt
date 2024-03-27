package com.example.cdihandheldscannerviewactivity.SearchExpirationDateAndLot

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.cdihandheldscannerviewactivity.AssignExpirationDateAndLot.AssignExpirationDateAndLotNumberViewModel
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.example.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.example.cdihandheldscannerviewactivity.databinding.FragmentSearchExpirationDateAndLotNumberBinding

class SearchExpirationDateAndLotNumberFragment : Fragment() {
    private lateinit var binding: FragmentSearchExpirationDateAndLotNumberBinding

    private val viewModel: AssignExpirationDateAndLotNumberViewModel by activityViewModels()
    private var shouldShowMessage = false
    private var hasSearchBeenMade = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_expiration_date_and_lot_number,
            container,
            false
        )

        setupUI()
        observeViewModel()

        return binding.root
    }
    override fun onResume() {
        super.onResume()
        binding.itemNumberEditText.text.clear()
        binding.BinNumberEditText.text.clear()

    }
    override fun onPause() {
        super.onPause()
        shouldShowMessage = true
    }
    private fun setupUI() {
        binding.searchButton.setOnClickListener {
            // Extract string values from EditText fields correctly
            val itemNumber = binding.itemNumberEditText.text.toString()
            val binNumber = binding.BinNumberEditText.text.toString()
            // Use extracted String values for checks and ViewModel operations
            if (itemNumber.isNotBlank() && binNumber.isNotBlank()) {
                hasSearchBeenMade = true
                shouldShowMessage = false
                viewModel.getItemInfo(itemNumber, binNumber)

            } else {
                AlerterUtils.startErrorAlerter(requireActivity(), "Make sure everything is filled")
            }
        }
    }

    private fun observeViewModel() {
        viewModel.opMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotBlank()) {
                val success = viewModel.opSuccess.value ?: false
                if (success) {
                    AlerterUtils.startSuccessAlert(requireActivity(), "", message)
                } else {
                    AlerterUtils.startErrorAlerter(requireActivity(), message)
                }
            }
        }
        viewModel.opSuccess.observe(viewLifecycleOwner) {success ->
            if (shouldShowMessage && !success) {
                AlerterUtils.startErrorAlerter(requireActivity(), viewModel.opMessage.value!!)
            }else if (!shouldShowMessage && success && hasSearchBeenMade){
                //AlerterUtils.startSuccessAlert(requireActivity(),"", viewModel.opMessage.value!!)
                view?.findNavController()?.navigate(R.id.action_SearchExpirationDateAndLotNumberFragment_to_AssignExpirationDateAndLotNumberFragment)
            }

        }

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLasAPICallSuccessful ->
            if (!wasLasAPICallSuccessful) {
                AlerterUtils.startErrorAlerter(requireActivity(), "There was an error with last operation. Try again.")
            }
        }
    }

}


