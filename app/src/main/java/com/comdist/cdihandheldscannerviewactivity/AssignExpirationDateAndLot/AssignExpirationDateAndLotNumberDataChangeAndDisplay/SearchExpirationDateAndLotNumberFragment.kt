package com.comdist.cdihandheldscannerviewactivity.AssignExpirationDateAndLot.AssignExpirationDateAndLotNumberDataChangeAndDisplay

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.comdist.cdihandheldscannerviewactivity.AssignExpirationDateAndLot.AssignExpirationDateAndLotNumberViewModel
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.DataClassesForAPICalls.ItemData
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
            container,false
        )

        setupUI()
        initObservers()
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
                viewModel.getItemInfo(itemNumber, binNumber,"")

            } else {
                AlerterUtils.startErrorAlerter(requireActivity(), "Make sure everything is filled")
            }
        }
    }


    private fun initObservers() {


        viewModel.opMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotBlank()) {
                val success = viewModel.opSuccess.value ?: false
                if (!success) {
                    AlerterUtils.startErrorAlerter(requireActivity(), message)
                } else{}
            }
        }

        viewModel.suggestions.observe(viewLifecycleOwner){newSuggestions ->
            if(newSuggestions.isNotEmpty())
                initItemNumberAutoCompleteTextView(newSuggestions)
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
                AlerterUtils.startNetworkErrorAlert(requireActivity())
            }
        }

    }

    private fun initItemNumberAutoCompleteTextView(newItemSuggestion: List<ItemData>){
        val arrayAdapterForAutoCompleteTextView =
            ItemSuggestionAdapter(
                requireContext(),
                newItemSuggestion
            )
        binding.itemNumberEditText.setAdapter(arrayAdapterForAutoCompleteTextView)
        binding.itemNumberEditText.threshold = 1
        binding.itemNumberEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                // Handle the Enter key press here
//                viewModel.setOrderNumber(orderNumberEditText.text.toString())
//                searchForOrder()
                true
            } else {
                false
            }
        }
    }


    class ItemSuggestionAdapter(context: Context, private val items: List<ItemData>) : ArrayAdapter<ItemData>(context, 0, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.sugestion_item_expiration_date_and_lot_number_view, parent, false)

            val itemNumberTextView = view.findViewById<TextView>(R.id.ItemNumberTextView)
            val descriptionTextView = view.findViewById<TextView>(R.id.descriptionTextView)
            val expirationDateTextView = view.findViewById<TextView>(R.id.orderDateValueTextView)
            val lotNumberTextView = view.findViewById<TextView>(R.id.dateWantedValueTextView)

            val item = items[position]
            itemNumberTextView.text = item.itemNumber
            descriptionTextView.text = item.itemDescription
            expirationDateTextView.text = item.expireDate
            lotNumberTextView.text = item.lotNumber ?: "N/A"

            return view
        }

        private val originalList = ArrayList(items)

        private val filter = object : Filter() {


            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val query = constraint?.toString()?.lowercase()
                val filteredList = if (query.isNullOrEmpty()) {
                    originalList
                } else {
                    originalList.filter {
                        it.itemNumber.lowercase().contains(query)
                    }
                }

                results.values = filteredList
                results.count = filteredList.size

                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clear()
                if (results?.count ?: 0 > 0) {
                    addAll(results?.values as List<ItemData>)
                    notifyDataSetChanged()

                } else {
                    notifyDataSetChanged()
                }

            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as ItemData).itemNumber
            }
        }
        override fun getFilter(): Filter {
            return filter
        }

    }

}


