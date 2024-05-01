package com.comdist.cdihandheldscannerviewactivity.ProductsInBin.ProductDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.comdist.cdihandheldscannerviewactivity.ProductsInBin.ProductsInBinViewModel
import com.comdist.cdihandheldscannerviewactivity.R
import com.comdist.cdihandheldscannerviewactivity.databinding.FragmentProductDetailsBinding

// Define the ProductDetailsFragment class, which is a subclass of Fragment
class ProductDetailsFragment : Fragment() {

    // Declare ViewModel and UI elements
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

    // onCreate lifecycle method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // onCreateView lifecycle method
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.products_in_bin_product_details_fragment, container, false)

        // Initialize UI elements
        initUIElements()

        // Change the names of detail views
        changeNamesOfDetailViews()

        // Fill all details with information
        fillAllDetailsWithInfo()

        // Fill the upper division with information
        fillUpperDivInfo()

        return binding.root
    }

    // Function to fill the upper division with information
    private fun fillUpperDivInfo(){
        val currentItem = viewModel.listOfProducts.value?.get(viewModel.currentlyChosenAdapterPosition.value!!)
        itemNametextView.text = "${currentItem?.itemName} - ${currentItem?.itemDetails}"
        itemNumberTextView.text = currentItem?.itemNumber
    }

    // Function to initialize UI elements
    private fun initUIElements(){
        // Initialize TextViews and Views by binding them to the layout
        binLocationTextView = binding.binLocation
        QtyonHandValueTextView = binding.qtyOnHandAndInPickingView.findViewById(R.id.detailContent)
        QtyInPickingValueTextView = binding.qtyOnHandAndInPickingView.findViewById(R.id.otherDetailContent)
        ExpirationDateDetailBlock = binding.expDateView
        BarCodeTextView = binding.barCode
        UOMValueTextView = binding.UOMAndUOMQtyView.findViewById(R.id.detailContent)
        QtyUOMValueTextView = binding.UOMAndUOMQtyView.findViewById(R.id.otherDetailContent)
        itemNumberTextView = binding.itemNumberTextView
        itemNametextView = binding.itemNameTextView
    }

    // Function to change the names of detail views
    private fun changeNamesOfDetailViews(){
        // Change the text of TextViews to display appropriate detail names
        binding.qtyOnHandAndInPickingView.findViewById<TextView>(R.id.detailName).text = "Quantity on Hand:"
        binding.qtyOnHandAndInPickingView.findViewById<TextView>(R.id.otherDetailName).text = "Quantity in Picking:"
        ExpirationDateDetailBlock.findViewById<TextView>(R.id.detailName).text = "Expiration Date:"
        binding.UOMAndUOMQtyView.findViewById<TextView>(R.id.detailName).text = "Unit Of Measurement:"
        binding.UOMAndUOMQtyView.findViewById<TextView>(R.id.otherDetailName).text = "Quantity in UOM:"
    }

    // Function to fill all details with information
    private fun fillAllDetailsWithInfo(){
        val currentItem = viewModel.listOfProducts.value?.get(viewModel.currentlyChosenAdapterPosition.value!!)

        // Format and set the bin location
        val formattedString = currentItem?.binLocation?.replace(Regex("(\\d+)([a-z])(\\d+)"), "\$1-\$2-\$3")
        binLocationTextView.text = formattedString

        // Set other product details
        if(currentItem?.barCode == null)
            BarCodeTextView.text = "N/A"
        else
            BarCodeTextView.text = currentItem.barCode.toString()
        QtyonHandValueTextView.text = currentItem?.quantityOnHand.toString()
        QtyInPickingValueTextView.text = currentItem?.quantityInPicking.toString()

        // TODO move the logic of assigning the Expiration Date of a product to be Not Available if the exp date is null, to the ViewModel to comply with the separation of concerns principle
        if (currentItem?.expirationDate == null){
            ExpirationDateDetailBlock.findViewById<TextView>(R.id.detailContent).text = "Not available"
        }
        else
            ExpirationDateDetailBlock.findViewById<TextView>(R.id.detailContent).text = currentItem?.expirationDate

        // Set Unit of Measurement and Quantity in UOM
        UOMValueTextView.text = currentItem?.unitOfMeasurement.toString()
        QtyUOMValueTextView.text = currentItem?.quantityInUOM.toString()
    }
}
