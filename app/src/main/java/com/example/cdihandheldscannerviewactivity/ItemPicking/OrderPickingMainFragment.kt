package com.example.cdihandheldscannerviewactivity.ItemPicking

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.example.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.example.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.example.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.example.cdihandheldscannerviewactivity.databinding.FragmentOrderPickingMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class orderPickingMainFragment : Fragment(), itemInOrderClickListener{

    private lateinit var binding: FragmentOrderPickingMainBinding
    private lateinit var orderNumberEditText: EditText
    private lateinit var searchOrderButton: Button
    private lateinit var fabScrollDown: FloatingActionButton
    private lateinit var finishOrderPickingButton: Button
    private val viewModel: ItemPickingViewModel by activityViewModels()

    private lateinit var adapter : ItemPickingAdapter



    private var hasPageJustStarted: Boolean = false
    var hasOrderBeenSearched: Boolean = false
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
        initUIElements()
        initObservers()

        adapter = ItemPickingAdapter(this)
        binding.totalPickingItemsList.adapter = adapter
        hasOrderBeenSearched = false
        hasPageJustStarted = true
        viewModel.setChosenAdapterPosition(0)

        // Retrieve company ID from shared preferences
        val companyID:String = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        viewModel.setCompanyIDFromSharedPref(companyID)
        val userNameOfPicker: String = SharedPreferencesUtils.getUserNameFromSharedPref(requireContext())
        viewModel.setUserNameOfPickerFromSharedPref(userNameOfPicker)

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
                    viewModel.endPickingTimer()
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

        progressDialog = Dialog(requireContext()).apply{
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
        }


    }

    private fun searchForOrder(){
        hasOrderBeenSearched = true
        progressDialog.show()
        viewModel.verifyIfOrderIsAvailableInBackend()

    }

    private fun initObservers() {

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

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLasAPICallSuccesful ->
            if (!wasLasAPICallSuccesful && hasOrderBeenSearched) {
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
            } else if (hasOrderBeenSearched) {
                viewModel.getItemsInOrder()
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

}