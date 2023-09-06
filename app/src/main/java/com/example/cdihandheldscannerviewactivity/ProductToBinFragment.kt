package com.example.cdihandheldscannerviewactivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.telecom.Call
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.cdihandheldscannerviewactivity.databinding.FragmentProductToBinBinding
import com.example.cdihandheldscannerviewactivity.network.ResponseWrapperWarehouse
import com.example.cdihandheldscannerviewactivity.network.ScannerAPI
import com.example.cdihandheldscannerviewactivity.network.WarehouseInfo
import java.util.Scanner
import retrofit2.Callback
import retrofit2.Response

class ProductToBinFragment : Fragment() {

    private lateinit var binding: FragmentProductToBinBinding
    private lateinit var listOfWarehouses: List<WarehouseInfo>
    private lateinit var warehouseSpinner: Spinner
    private lateinit var binNumberEditText: EditText
    private lateinit var searchButton: Button
    private var isSpinnerArrowUp: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun fillSpinnerWithWarehouses(){
        val warehouses = mutableListOf<String>()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, warehouses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        warehouseSpinner.adapter = adapter
        for (aWarehouse in listOfWarehouses) {
            warehouses.add(aWarehouse.warehouseName)
        }
        adapter.notifyDataSetChanged()
    }

    private fun getWarehousesFromBackendForSpinner(){
        ScannerAPI.retrofitService.getWarehousesAvailable().enqueue(object: Callback<ResponseWrapperWarehouse>{
            override fun onResponse(
                call: retrofit2.Call<ResponseWrapperWarehouse>,
                response: Response<ResponseWrapperWarehouse>
            ) {
                listOfWarehouses = response.body()!!.response.warehouses.warehouses
                fillSpinnerWithWarehouses()
            }

            override fun onFailure(call: retrofit2.Call<ResponseWrapperWarehouse>, t: Throwable) {
                println("Error -> " + t.message)
                Toast.makeText(requireContext(), "There was an error getting the warehouses in the network!", Toast.LENGTH_SHORT).show()
            }

        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSpinner(){

        val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            // Reset background when spinner closes
            warehouseSpinner.setBackgroundResource(R.drawable.drop_down_background)
            isSpinnerArrowUp = false
        }

        // set what happens whenever the Spinner is clicked
        warehouseSpinner.setOnTouchListener{view, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    warehouseSpinner.setBackgroundResource(R.drawable.drop_down_arrow_up)
                    warehouseSpinner.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
                    isSpinnerArrowUp = true
                }
            }
            false
        }

        // set what happens whenever an item is clicked in the spinner
        warehouseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                warehouseSpinner.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
                isSpinnerArrowUp = false
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        // Reset the background when the user touches outside of the Spinner
        binding.root.setOnTouchListener { _, _ ->
            if (isSpinnerArrowUp) {
                warehouseSpinner.setBackgroundResource(R.drawable.drop_down_background)
                warehouseSpinner.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
                isSpinnerArrowUp = false
            }
            false
        }
    }


// TODO (3) Anñadir funcionalidad que haga que el formato del editText del Bin Number siempre tenga los guiones entremedio (ver commentarios en Figma)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_to_bin, container, false)
        warehouseSpinner = binding.warehouseSpinner
        binNumberEditText = binding.binNumberEditText
        searchButton = binding.searchBinButton

        getWarehousesFromBackendForSpinner()
        initSpinner()

        return binding.root
    }

}