package com.comdist.cdihandheldscannerviewactivity.AssignExpirationDateAndLot.AssignExpirationDateAndLotNumberDataChangeAndDisplay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.comdist.cdihandheldscannerviewactivity.AssignExpirationDateAndLot.AssignExpirationDateAndLotNumberViewModel
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.comdist.cdihandheldscannerviewactivity.databinding.FragmentSearchExpirationDateAndLotNumberBinding


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


