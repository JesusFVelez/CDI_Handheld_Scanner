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
import com.example.cdihandheldscannerviewactivity.databinding.HomeScreenFragmentBinding
import com.example.cdihandheldscannerviewactivity.login.loginActivity

class HomeScreenFragment : Fragment() {

    private lateinit var binding: HomeScreenFragmentBinding
    private lateinit var logOutButton: Button
    private lateinit var productToBinButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.home_screen_fragment, container, false)
        logOutButton = binding.logOutButton
        productToBinButton = binding.productToBinButton

        logOutButton.setOnClickListener{
            AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes") { _, _ ->

                    val intent = Intent(this.activity, loginActivity::class.java)
                    startActivity(intent)
                    this.activity?.finish()
                }
                .setNegativeButton("No", null)
                .show()
        }

        productToBinButton.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeScreenFragment_to_productToBinFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}