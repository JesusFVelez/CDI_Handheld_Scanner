package com.example.cdihandheldscannerviewactivity.ItemPicking

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.cdihandheldscannerviewactivity.ProductsInBin.ProductsInBinViewModel
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.example.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.example.cdihandheldscannerviewactivity.databinding.FragmentOrderPickingMainBinding

class orderPickingMainFragment : Fragment() {

    private lateinit var binding: FragmentOrderPickingMainBinding
    private lateinit var orderNumberEditText: EditText
    private lateinit var searchOrderButton: Button
    private val viewModel: ItemPickingViewModel by activityViewModels()

    // Network-related variables
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback : ConnectivityManager.NetworkCallback
    private lateinit var networkRequest: NetworkRequest

    private var hasPageJustStarted: Boolean = false
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
        initNetworkRelatedComponents()


        return binding.root


    }


    override fun onResume() {
        super.onResume()

        hasPageJustStarted = false
        // Register the callback
        if (connectivityManager != null) {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        }
    }


    // Handle onPause lifecycle event
    override fun onPause() {
        super.onPause()

        // Unregister the callback
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    private fun initNetworkRelatedComponents(){
        // Initialize network-related components
        connectivityManager = requireContext().getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkRequest = NetworkRequest.Builder().build()
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            // Handle network availability
            override fun onAvailable(network: Network) {
                // Handle connection
                if (hasPageJustStarted)
                    AlerterUtils.startInternetRestoredAlert(requireActivity())
                else
                    hasPageJustStarted = true

            }
            // Handle network loss
            override fun onLost(network: Network) {
                // Handle disconnection
                hasPageJustStarted = true
                AlerterUtils.startInternetLostAlert(requireActivity())
            }
        }
    }

    private fun initUIElements(){
        searchOrderButton = binding.searchOrderButton
        searchOrderButton.setOnClickListener{

        }


    }

}