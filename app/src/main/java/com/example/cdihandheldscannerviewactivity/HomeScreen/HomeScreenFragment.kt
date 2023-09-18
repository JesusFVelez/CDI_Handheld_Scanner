package com.example.cdihandheldscannerviewactivity.HomeScreen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Storage.BundleUtils
import com.example.cdihandheldscannerviewactivity.databinding.HomeScreenFragmentBinding
import com.example.cdihandheldscannerviewactivity.login.loginActivity

// Home screen fragment class
class HomeScreenFragment : Fragment() {

    // Variables for binding and buttons
    private lateinit var binding: HomeScreenFragmentBinding
    private lateinit var logOutButton: Button
    private lateinit var productToBinButton: Button

    // Method called when the fragment is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // Method to create and return the view hierarchy associated with the fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.home_screen_fragment, container, false)
        // Initialize the buttons
        logOutButton = binding.logOutButton
        productToBinButton = binding.productToBinButton

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
            val bundle = BundleUtils.getBundleToSendFragmentNameToNextFragment("HomeScreen")
            it.findNavController().navigate(R.id.action_homeScreenFragment_to_productToBinFragment, bundle)
        }

        // Return the view for this fragment
        return binding.root
    }

    // Method called immediately after onCreateView() has returned, and fragment's view hierarchy has been instantiated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
