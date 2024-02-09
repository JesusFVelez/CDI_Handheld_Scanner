package com.example.cdihandheldscannerviewactivity.HomeScreen

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.example.cdihandheldscannerviewactivity.Utils.Network.RPMAccessResponseWrapper
import com.example.cdihandheldscannerviewactivity.Utils.Network.ScannerAPI
import com.example.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.example.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.example.cdihandheldscannerviewactivity.databinding.FragmentHomeScreenBinding
import com.example.cdihandheldscannerviewactivity.login.LoginViewModel
import com.example.cdihandheldscannerviewactivity.login.loginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Home screen fragment class
class HomeScreenFragment : Fragment() {

    // Variables for binding and buttons
    private lateinit var binding: FragmentHomeScreenBinding
    private lateinit var logOutButton: Button
    private lateinit var productToBinButton: Button
    private lateinit var binsWithProductButton: Button
    private lateinit var itemPickingButton: Button
    // Dialog for showing progress
    private lateinit var progressDialog: Dialog

    private lateinit var viewModel: HomeScreenViewModel
    private lateinit var chosenMenuOption: HomeScreenViewModel.MenuOptionDataClass


    // Method called when the fragment is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCircularRevealAnim(view)
    }

    private fun initCircularRevealAnim(view:View){
        view.post {

            // Get the center for the clipping circle
            val cx = (view.left + view.right) / 2
            val cy = (view.top + view.bottom) / 2

            // Get the final radius for the clipping circle
            val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()

            // Create the animator for this view (the start radius is zero)
            val anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius)

            // Set a longer duration for the animation
            anim.setDuration(500);  // Duration in milliseconds, e.g., 1000ms = 1 second

            // Make the view visible and start the animation
            view.visibility = View.VISIBLE
            anim.start()
        }
    }



    private fun navigateToMenuOption( menuOptionNavigationAction: Int){
        val bundle = BundleUtils.getBundleToSendFragmentNameToNextFragment("HomeScreen")
        findNavController().navigate(menuOptionNavigationAction, bundle)
    }


    private fun initObservers(){
        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner){wasLastApiCallSuccessful ->
            if(!wasLastApiCallSuccessful){
                progressDialog.dismiss()
                AlerterUtils.startNetworkErrorAlert(requireActivity())
                Log.i("API Call", "API Call did not work")
            }
        }

        viewModel.doesUserHaveAccessToMenuOption.observe(viewLifecycleOwner){doesUserHaveAccessToMenuOption ->
            progressDialog.dismiss()
            if(doesUserHaveAccessToMenuOption){
                navigateToMenuOption(viewModel.currentlyChosenMenuOption.value!!.menuOptionNavigationAction)
            }else{
                AlerterUtils.startErrorAlerter(requireActivity(), viewModel.errorMessageForUserAccess.value!!)
            }

        }
    }
    private fun initViewModel(){
        // Setting the view model
        viewModel = ViewModelProvider(this)[HomeScreenViewModel::class.java]

        val companyID = SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
        viewModel.setCompanyIDFromSharedPref(companyID)

        val userName = SharedPreferencesUtils.getUserNameFromSharedPref(requireContext())
        viewModel.setUserNameOfPickerFromSharedPref(userName)
    }

    private fun initUIElements(){
        logOutButton = binding.logOutButton
        productToBinButton = binding.productToBinButton
        binsWithProductButton = binding.BinsWithItemButton
        itemPickingButton = binding.ItemPickingButton

        progressDialog = Dialog(requireContext()).apply{
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
        }

        // Set click listener for the log out button to show a logout confirmation dialog
        logOutButton.setOnClickListener{
            AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes") { _, _ ->

                    // Log out and navigate to the login activity
                    val intent = Intent(this.activity, loginActivity::class.java)
                    startActivity(intent)
                    this.activity?.finish()
                }
                .setNegativeButton("No", null)
                .show()
        }

        // Set click listener for the product to bin button to navigate to the ProductToBinFragment
        productToBinButton.setOnClickListener{
            menuButtonClickHandler(HomeScreenViewModel.MenuOptions.ProductInBinMenuOption)
        }
        // Set click listener for the product to bin button to navigate to the SearchBinsWithProductFragment
        binsWithProductButton.setOnClickListener{
            menuButtonClickHandler(HomeScreenViewModel.MenuOptions.BinsWithProductMenuOption)
        }

        itemPickingButton.setOnClickListener{
            menuButtonClickHandler(HomeScreenViewModel.MenuOptions.ItemPickingMenuOption)
        }
    }


    private fun menuButtonClickHandler(menuOption: HomeScreenViewModel.MenuOptionDataClass){
        progressDialog.show()
        viewModel.setCurrentlyChosenMenuOption(menuOption.menuOptionName, menuOption.menuOptionNavigationAction)
        viewModel.verifyInBackendIfUserHasAccessToMenuOption(menuOption)
    }
    // Method to create and return the view hierarchy associated with the fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_screen, container, false)



        initUIElements()
        initViewModel()
        initObservers()

        // Return the view for this fragment
        return binding.root
    }





}
