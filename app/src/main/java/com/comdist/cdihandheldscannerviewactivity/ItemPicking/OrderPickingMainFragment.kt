package com.comdist.cdihandheldscannerviewactivity.ItemPicking

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Filter
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.Network.ordersThatAreInPickingClass
import com.comdist.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.comdist.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.comdist.cdihandheldscannerviewactivity.databinding.FragmentOrderPickingMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class orderPickingMainFragment : Fragment(), itemInOrderClickListener{

    private lateinit var binding: FragmentOrderPickingMainBinding
    private lateinit var orderNumberEditText: AutoCompleteTextView
    private lateinit var searchOrderButton: Button
    private lateinit var fabScrollDown: FloatingActionButton
    private lateinit var finishOrderPickingButton: Button
    private val viewModel: ItemPickingViewModel by activityViewModels()

    private lateinit var adapter : ItemPickingAdapter



    private var hasPageJustStarted: Boolean = false
    private var hasOrderBeenSearched: Boolean = false
    private lateinit var progressDialog: Dialog


    fun verifyIfOrderIsBeingPicked(): Boolean{
        val orderNumber = orderNumberEditText.text.toString()
        return orderNumber != "" && hasOrderBeenSearched
    }

    fun showErrorMessageWhenExitingScreenWithoutFinishingPicking(){
        val orderNumber = orderNumberEditText.text.toString()
        AlerterUtils.startErrorAlerter(
            requireActivity(),
            "Cannot exit Item picking without finishing picking for order '${orderNumber}'"
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_picking_main, container, false)

        // Retrieve company ID from shared preferences
        val companyID:String = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        viewModel.setCompanyIDFromSharedPref(companyID)
        val userNameOfPicker: String = SharedPreferencesUtils.getUserNameFromSharedPref(requireContext())
        viewModel.setUserNameOfPickerFromSharedPref(userNameOfPicker)


        initUIElements()
        initObservers()


        hasOrderBeenSearched = false
        hasPageJustStarted = true
        viewModel.setChosenAdapterPosition(0)



        return binding.root


    }


    override fun onResume() {
        super.onResume()

        hasPageJustStarted = false
        val bundle = arguments
        val lastFragmentName : String = BundleUtils.getPastFragmentNameFromBundle(bundle)
        if(lastFragmentName == "HomeScreen"){
            viewModel.clearListOfItems()
            bundle?.clear()
        }

        // For whenever the fragment comes back from the pick individual fragmemt screen
        if(lastFragmentName == "null"){
            searchForOrder()
        }

    }


    // Handle onPause lifecycle event
    override fun onPause() {
        super.onPause()

    }




    private fun initUIElements(){


        searchOrderButton = binding.searchOrderButton
        searchOrderButton.setOnClickListener{
            viewModel.setOrderNumber(orderNumberEditText.text.toString())
            searchForOrder()
        }
        finishOrderPickingButton = binding.finishPickingButton
        finishOrderPickingButton.setOnClickListener {
                val popupWindow = PopupWindowUtils.createQuestionPopup(requireContext(), "Finished Picking the Order?", "Confirmation")
                popupWindow.contentView.findViewById<Button>(R.id.YesButton).setOnClickListener{
                    viewModel.updatePickingTimer("end")
                    viewModel.clearListOfItems()
                    findNavController().navigateUp()
                    AlerterUtils.startSuccessAlert(requireActivity(), "Finished Picking","Order has been successfully picked")
                    popupWindow.dismiss()
                }

                popupWindow.contentView.findViewById<Button>(R.id.NoButton).setOnClickListener{
                    popupWindow.dismiss()
                }

                popupWindow.showAtLocation(requireView(), Gravity.CENTER, 0, 0)

        }
        fabScrollDown = binding.fabScrollDown
        fabScrollDown.setOnClickListener{
            binding.ScrollVIew.post{
                binding.ScrollVIew.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }


        binding.ScrollVIew.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY == (binding.ScrollVIew.getChildAt(0).measuredHeight - binding.ScrollVIew.measuredHeight)) {
                fabScrollDown.hide()
            } else {
                fabScrollDown.show()
            }
        })
        orderNumberEditText = binding.orderNumberEditText
        viewModel.getAllOrdersThatHavePickingForSuggestions()


        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        adapter = ItemPickingAdapter(this)
        binding.totalPickingItemsList.adapter = adapter

    }

    private fun searchForOrder(){
        hasOrderBeenSearched = true
        progressDialog.show()
        viewModel.verifyIfOrderIsAvailableInBackend()

    }

    private fun initOrderNumberAutoCompleteTextView(newOrdersThatHavePicking: List<ordersThatAreInPickingClass>){
        val arrayAdapterForAutoCompleteTextView = CustomOrderSuggestionAdapter(requireContext(),newOrdersThatHavePicking)
        orderNumberEditText.setAdapter(arrayAdapterForAutoCompleteTextView)
        orderNumberEditText.threshold = 1
        orderNumberEditText.requestFocus()
        orderNumberEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                // Handle the Enter key press here
                viewModel.setOrderNumber(orderNumberEditText.text.toString())
                searchForOrder()
                true
            } else {
                false
            }
        }

    }

    private fun initObservers() {
        viewModel.ordersThatHavePicking.observe(viewLifecycleOwner){newOrdersThatHavePicking ->
            if(newOrdersThatHavePicking.isNotEmpty())
                initOrderNumberAutoCompleteTextView(newOrdersThatHavePicking)
        }

        viewModel.wasBinConfirmed.observe(viewLifecycleOwner) { wasBinConfirmed ->
            if (wasBinConfirmed && !hasPageJustStarted) {
                val bundle =
                    BundleUtils.getBundleToSendFragmentNameToNextFragment("OrderPickingMainFragment")
                findNavController().navigate(
                    R.id.action_orderPickingMainFragment_to_orderPickingItemFragment,
                    bundle
                )
            } else if (!hasPageJustStarted)
                AlerterUtils.startErrorAlerter(
                    requireActivity(),
                    viewModel.errorMessage.value!!["confirmBin"]!!
                )
        }

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLasAPICallSuccessful ->
            if (!wasLasAPICallSuccessful && hasOrderBeenSearched) {
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity())
            }
        }

        viewModel.wasOrderFound.observe(viewLifecycleOwner) { isOrderInBackend ->
            if (isOrderInBackend && hasOrderBeenSearched) {
                viewModel.verifyIfOrderHasPickingInBackend()
            } else if (hasOrderBeenSearched) {
                progressDialog.dismiss()
                AlerterUtils.startErrorAlerter(
                    requireActivity(),
                    viewModel.errorMessage.value!!["confirmOrder"]!!
                )
            }
        }

        viewModel.orderHasPicking.observe(viewLifecycleOwner) { doesOrderHasPicking ->
            if (doesOrderHasPicking && hasOrderBeenSearched) {
                viewModel.verifyIfClientAccountIsClosedInBackend()
            } else if (hasOrderBeenSearched) {
                progressDialog.dismiss()
                AlerterUtils.startErrorAlerter(
                    requireActivity(),
                    viewModel.errorMessage.value!!["verifyIfOrderHasPicking"]!!
                )
            }

        }

        viewModel.wasClientAccountClosed.observe(viewLifecycleOwner) { wasClientAccountClosed ->

            if (wasClientAccountClosed && hasOrderBeenSearched) {
                progressDialog.dismiss()
                AlerterUtils.startErrorAlerter(
                    requireActivity(),
                    viewModel.errorMessage.value!!["verifyIfClientAccountIsClosed"]!!
                )
            } else if (hasOrderBeenSearched ) {
                viewModel.getItemsInOrder()
                if(!viewModel.hasPickingTimerAlreadyStarted.value!!)
                    viewModel.startPickingTimer()
                finishOrderPickingButton.isEnabled = true
            }
        }

        viewModel.listOfItemsInOrder.observe(viewLifecycleOwner) { newListOfItemsInOrder ->
            newListOfItemsInOrder.let {
                progressDialog.dismiss()
                adapter.data = it
            }
            if(!newListOfItemsInOrder.isEmpty())
                fabScrollDown.visibility = View.VISIBLE
            else
                fabScrollDown.visibility = View.GONE
        }
    }
    override fun onItemClickListener(view: View, position: Int) {
        val listener = object : PopupInputListener {
            override fun onConfirm(input: EditText) {
                viewModel.setChosenAdapterPosition(position)
                viewModel.setCurrentlyChosenItem()
                viewModel.confirmBin(input.text.toString(), position)
            }
        }
        PopupWindowUtils.showConfirmationPopup(requireContext(), view, "Scan Bin '" + viewModel.listOfItemsInOrder.value!![position].binLocation + "' to continue", "Bin Number", listener)
    }

    interface PopupInputListener{
        fun onConfirm(input: EditText)
    }

    class CustomOrderSuggestionAdapter(context: Context, private var suggestions: List<ordersThatAreInPickingClass>): ArrayAdapter<ordersThatAreInPickingClass>(context, 0, suggestions){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.order_picking_suggestion_view, parent, false)
            val orderNumberTextView = view.findViewById<TextView>(R.id.orderNumberTextView)
            val customerTextView = view.findViewById<TextView>(R.id.customerNameTextView)
            val orderDateTextView = view.findViewById<TextView>(R.id.orderDateValueTextView)
            val dateWantedTextView = view.findViewById<TextView>(R.id.dateWantedValueTextView)


            val item = suggestions[position]
            orderNumberTextView.text = item.orderNumber
            customerTextView.text = item.customerName
            orderDateTextView.text = item.orderedDate
            dateWantedTextView.text = item.dateWanted


            return view
        }
        private val originalList = ArrayList(suggestions)


        private val filter = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val query = constraint?.toString()?.lowercase()
                val filteredList = if (query.isNullOrEmpty()) {
                    originalList
                } else {
                    originalList.filter {
                        it.orderNumber.lowercase().contains(query)
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
                    addAll(results?.values as List<ordersThatAreInPickingClass>)
                    notifyDataSetChanged()

                } else {
                    notifyDataSetChanged()
                }

            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as ordersThatAreInPickingClass).orderNumber
            }
        }
        override fun getFilter(): Filter {
            return filter

        }

    }

}