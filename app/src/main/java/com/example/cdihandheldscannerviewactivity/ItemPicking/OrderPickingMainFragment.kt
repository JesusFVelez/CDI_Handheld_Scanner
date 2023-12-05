package com.example.cdihandheldscannerviewactivity.ItemPicking

import android.app.Dialog
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.cdihandheldscannerviewactivity.ProductsInBin.ProductsInBinAdapter
import com.example.cdihandheldscannerviewactivity.ProductsInBin.ProductsInBinViewModel
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.example.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.example.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.example.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.example.cdihandheldscannerviewactivity.databinding.FragmentOrderPickingMainBinding

class orderPickingMainFragment : Fragment(), itemInOrderClickListener{

    private lateinit var binding: FragmentOrderPickingMainBinding
    private lateinit var orderNumberEditText: EditText
    private lateinit var searchOrderButton: Button
    private val viewModel: ItemPickingViewModel by activityViewModels()

    private lateinit var adapter : ItemPickingAdapter
    private var hasPageJustStarted: Boolean = false



    private var hasOrderBeenSearched: Boolean = false
    private lateinit var progressDialog: Dialog
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

    }


    // Handle onPause lifecycle event
    override fun onPause() {
        super.onPause()

    }



    private fun initUIElements(){
        searchOrderButton = binding.searchOrderButton
        orderNumberEditText = binding.orderNumberEditText
        orderNumberEditText.requestFocus()
        orderNumberEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                // Handle the Enter key press here
               searchForOrder()
                true
            } else {
                false
            }
        }
        searchOrderButton.setOnClickListener{
            searchForOrder()
        }
        progressDialog = Dialog(requireContext()).apply{
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
        }


    }

    private fun searchForOrder(){
        hasOrderBeenSearched = true
        progressDialog.show()
        viewModel.verifyIfOrderIsAvailableInBackend(orderNumberEditText.text.toString())
    }

    private fun initObservers(){

        viewModel.wasBinConfirmed.observe(viewLifecycleOwner){wasBinConfirmed ->
            if(wasBinConfirmed) {
                val bundle = BundleUtils.getBundleToSendFragmentNameToNextFragment("orderPickingMainFragment")
                findNavController().navigate(
                    R.id.action_orderPickingMainFragment_to_orderPickingItemFragment,
                    bundle
                )
            }
        }

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner){ wasLasAPICallSuccesful ->
            if(!wasLasAPICallSuccesful && hasOrderBeenSearched){
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity())
            }
        }

        viewModel.wasOrderFound.observe(viewLifecycleOwner){ isOrderInBackend ->
            if(isOrderInBackend && hasOrderBeenSearched){
                viewModel.verifyIfOrderHasPickingInBackend(orderNumberEditText.text.toString())
            }
            else if(hasOrderBeenSearched) {
                progressDialog.dismiss()
                AlerterUtils.startErrorAlerter(
                    requireActivity(),
                    viewModel.errorMessage.value!!["confirmOrder"]!!
                )
            }
        }

        viewModel.orderHasPicking.observe(viewLifecycleOwner){ doesOrderHasPicking ->
            if(doesOrderHasPicking && hasOrderBeenSearched){
                viewModel.verifyIfClientAccountIsClosedInBackend(orderNumberEditText.text.toString())
            }
            else if(hasOrderBeenSearched) {
                    progressDialog.dismiss()
                    AlerterUtils.startErrorAlerter(
                        requireActivity(),
                        viewModel.errorMessage.value!!["verifyIfOrderHasPicking"]!!
                    )
                }

        }

        viewModel.wasClientAccountClosed.observe(viewLifecycleOwner){wasClientAccountClosed ->

            if(wasClientAccountClosed && hasOrderBeenSearched){
                progressDialog.dismiss()
                AlerterUtils.startErrorAlerter(requireActivity(), viewModel.errorMessage.value!!["verifyIfClientAccountIsClosed"]!!)
            }else if (hasOrderBeenSearched)
                viewModel.getItemsInOrder(orderNumberEditText.text.toString())
        }

        viewModel.listOfItemsInOrder.observe(viewLifecycleOwner){ newListOfItemsInOrder ->
            if(hasOrderBeenSearched)
                newListOfItemsInOrder.let{
                    progressDialog.dismiss()
                    adapter.data = it
                }
        }
    }

    override fun onItemClickListener(view: View, position: Int) {
        // TODO - Checkeate a ver como puedes presentar el popup message de confirm bin (ya cree el popup message en el folder de layout, usalo de para el todo de crear el popup message de item number referencia. Se llama "confirm_bin_popup.xml" )antes de ir al proximo fragment (orderPickingItemFragment)
        // TODO - averiguarte como hacer que cuando le des "ok", llames el API de confirm bin y valida que este bien el bin y despues que entre al fragment de "orderPickingItemFragment"
        val confirmButtonOnClickListener = OnClickListener {
            val binConfirmationEditText = it.findViewById<EditText>(R.id.confirmationEditText)
            viewModel.confirmBin(orderNumberEditText.text.toString(), binConfirmationEditText.text.toString(), position)
        }
        PopupWindowUtils.showConfirmationPopup(requireContext(), view, "Confirm Bin", "Bin Number", confirmButtonOnClickListener)


    }

}