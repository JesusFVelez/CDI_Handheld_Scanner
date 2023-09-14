package com.example.cdihandheldscannerviewactivity.ProductsInBin.ProductDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextClock
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.cdihandheldscannerviewactivity.ProductsInBin.ProductsInBinViewModel
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.databinding.FragmentProductDetailsBinding


class ProductDetailsFragment : Fragment() {

    private val viewModel: ProductsInBinViewModel by activityViewModels()
    private lateinit var binding: FragmentProductDetailsBinding
    private lateinit var binLocationTextView: TextView
    private lateinit var QtyonHandValueTextView: TextView
    private lateinit var QtyInPickingValueTextView: TextView
    private lateinit var ExpirationDateDetailBlock: View
    private lateinit var BarCodeTextView: TextView
    private lateinit var UOMValueTextView: TextView
    private lateinit var QtyUOMValueTextView: TextView
    private lateinit var itemNametextView: TextView
    private lateinit var itemNumberTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_details, container, false)
    initUIElements()
    changeNamesOfDetailViews()
    fillAllDetailsWithInfo()
    fillUpperDivInfo()



    return binding.root
    }


    private fun fillUpperDivInfo(){
        val currentItem = viewModel.listOfProducts.value?.get(viewModel.currentlyChosenAdapterPosition.value!!)
        itemNametextView.text = "${currentItem?.itemName} - ${currentItem?.itemDetails}"
        itemNumberTextView.text = currentItem?.itemNumber
    }
    private fun initUIElements(){
        binLocationTextView = binding.binLocation
        QtyonHandValueTextView = binding.qtyOnHandAndInPickingView.findViewById(R.id.detailContent)
        QtyInPickingValueTextView = binding.qtyOnHandAndInPickingView.findViewById(R.id.otherDetailContent)
        ExpirationDateDetailBlock = binding.expDateView
        BarCodeTextView = binding.barCode
        UOMValueTextView = binding.UOMAndUOMQtyView.findViewById(R.id.detailContent)
        QtyUOMValueTextView = binding.UOMAndUOMQtyView.findViewById(R.id.otherDetailContent)
        itemNumberTextView = binding.itemNumber
        itemNametextView = binding.itemName
    }





    private fun changeNamesOfDetailViews(){
        binding.qtyOnHandAndInPickingView.findViewById<TextView>(R.id.detailName).text = "Quantity on Hand:"
        binding.qtyOnHandAndInPickingView.findViewById<TextView>(R.id.otherDetailName).text = "Quantity in Picking:"
        ExpirationDateDetailBlock.findViewById<TextView>(R.id.detailName).text = "Expiration Date:"
        binding.UOMAndUOMQtyView.findViewById<TextView>(R.id.detailName).text = "Unit Of Measurement:"
        binding.UOMAndUOMQtyView.findViewById<TextView>(R.id.otherDetailName).text = "Quantity in UOM:"
    }

    private fun fillAllDetailsWithInfo(){
        val currentItem = viewModel.listOfProducts.value?.get(viewModel.currentlyChosenAdapterPosition.value!!)
        val formattedString = currentItem?.binLocation?.replace(Regex("(\\d+)([a-z])(\\d+)"), "\$1-\$2-\$3")
        binLocationTextView.text = formattedString
        BarCodeTextView.text = currentItem?.barCode.toString()
        QtyonHandValueTextView.text = currentItem?.quantityOnHand.toString()
        QtyInPickingValueTextView.text = currentItem?.quantityInPicking.toString()
        // TODO move the logic of assigning the Expiration Date of a product to be Not Available if the exp date is null, to the ViewModel to comply with the separation of concerns principle
        if (currentItem?.expirationDate == null){
            ExpirationDateDetailBlock.findViewById<TextView>(R.id.detailContent).text = "Not available"
        }
        else
            ExpirationDateDetailBlock.findViewById<TextView>(R.id.detailContent).text = currentItem?.expirationDate
        UOMValueTextView.text = currentItem?.unitOfMeasurement.toString()
        QtyUOMValueTextView.text = currentItem?.quantityInUOM.toString()
    }


}