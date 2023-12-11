package com.example.cdihandheldscannerviewactivity.HomeScreen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.example.cdihandheldscannerviewactivity.databinding.FragmentHomeScreenBinding
import com.example.cdihandheldscannerviewactivity.login.loginActivity


// Home screen fragment class
class HomeScreenFragment : Fragment() {

    // Variables for binding and buttons
    private lateinit var binding: FragmentHomeScreenBinding
    private lateinit var logOutButton: Button
    private lateinit var productToBinButton: Button
    private lateinit var binsWithProductButton: Button
    private lateinit var itemPickingButton: Button

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



    // Method to create and return the view hierarchy associated with the fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_screen, container, false)
        // Initialize the buttons
        logOutButton = binding.logOutButton
        productToBinButton = binding.productToBinButton
        binsWithProductButton = binding.BinsWithItemButton
        itemPickingButton = binding.ItemPickingButton

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
        // Set click listener for the product to bin button to navigate to the SearchBinsWithProductFragment
        binsWithProductButton.setOnClickListener{
            val bundle = BundleUtils.getBundleToSendFragmentNameToNextFragment("HomeScreen")
            it.findNavController().navigate(R.id.action_homeScreenFragment_to_searchBinsWithProductFragment, bundle)
        }

        itemPickingButton.setOnClickListener{
            val bundle = BundleUtils.getBundleToSendFragmentNameToNextFragment("HomeScreen")
            it.findNavController().navigate(R.id.action_homeScreenFragment_to_orderPickingMainFragment, bundle)
        }





        // Return the view for this fragment
        return binding.root
    }



}
