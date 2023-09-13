package com.example.cdihandheldscannerviewactivity.ProductsInBin.ProductDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.cdihandheldscannerviewactivity.ProductsInBin.ProductsInBinViewModel
import com.example.cdihandheldscannerviewactivity.R
import com.example.cdihandheldscannerviewactivity.databinding.FragmentProductDetailsBinding
import org.w3c.dom.Text


class ProductDetailsFragment : Fragment() {

    private val viewModel: ProductsInBinViewModel by activityViewModels()
    private lateinit var binding: FragmentProductDetailsBinding
    private lateinit var binLocationTextView: TextView
    private lateinit var QtyonHandDetailBlock: View
    private lateinit var QtyInPickingDetailBlock: View
    private lateinit var ExpirationDateDetailBlock: View
    private lateinit var BarCodeTextView: TextView
    private lateinit var UOMDetailBlock: View
    private lateinit var QtyUOMDetailBlock: View
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
    binding.qtyOnHandTxt.findViewById<TextView>(R.id.detailName).text = "Quantity On Hand:"

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
        QtyonHandDetailBlock = binding.qtyOnHandTxt
        QtyInPickingDetailBlock = binding.qtyInPicking
        ExpirationDateDetailBlock = binding.expDate
        BarCodeTextView = binding.barCode
        UOMDetailBlock = binding.UOM
        QtyUOMDetailBlock = binding.qtyInUOM
        itemNumberTextView = binding.itemNumber
        itemNametextView = binding.itemName
    }





    private fun changeNamesOfDetailViews(){
        QtyonHandDetailBlock.findViewById<TextView>(R.id.detailName).text = "Quantity on Hand:"
        QtyInPickingDetailBlock.findViewById<TextView>(R.id.detailName).text = "Quantity in Picking:"
        ExpirationDateDetailBlock.findViewById<TextView>(R.id.detailName).text = "Expiration Date:"
        UOMDetailBlock.findViewById<TextView>(R.id.detailName).text = "Unit Of Measurement (UOM):"
        QtyUOMDetailBlock.findViewById<TextView>(R.id.detailName).text = "Quantity in UOM:"
    }

    private fun fillAllDetailsWithInfo(){
        val currentItem = viewModel.listOfProducts.value?.get(viewModel.currentlyChosenAdapterPosition.value!!)
        val formattedString = currentItem?.binLocation?.replace(Regex("(\\d+)([a-z])(\\d+)"), "\$1-\$2-\$3")
        binLocationTextView.text = formattedString
        BarCodeTextView.text = currentItem?.barCode.toString()


        QtyonHandDetailBlock.findViewById<TextView>(R.id.detailContent).text = currentItem?.quantityOnHand.toString()
        QtyInPickingDetailBlock.findViewById<TextView>(R.id.detailContent).text = currentItem?.quantityInPicking.toString()
        // TODO move the logic of assigning the Expiration Date of a product to be Not Available if the exp date is null, to the ViewModel to comply with the separation of concerns principle
        if (currentItem?.expirationDate == null){
            ExpirationDateDetailBlock.findViewById<TextView>(R.id.detailContent).text = "Not available"
        }
        else
            ExpirationDateDetailBlock.findViewById<TextView>(R.id.detailContent).text = currentItem?.expirationDate
        UOMDetailBlock.findViewById<TextView>(R.id.detailContent).text = currentItem?.unitOfMeasurement.toString()
        QtyUOMDetailBlock.findViewById<TextView>(R.id.detailContent).text = currentItem?.quantityInUOM.toString()
    }


}