package com.scannerapp.cdihandheldscannerviewactivity.EditItem.EditItemInformationPage

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.print.PrintHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.scannerapp.cdihandheldscannerviewactivity.EditItem.EditItemViewModel
import com.scannerapp.cdihandheldscannerviewactivity.R
import com.scannerapp.cdihandheldscannerviewactivity.Utils.AlerterUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.DateUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.PopupWindowUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage.BundleUtils
import com.scannerapp.cdihandheldscannerviewactivity.Utils.Storage.SharedPreferencesUtils
import com.scannerapp.cdihandheldscannerviewactivity.databinding.EditItemEditItemDetailsFragmentBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditItemDetailsFragment : Fragment() {
    private lateinit var binding: EditItemEditItemDetailsFragmentBinding

    /*Batch variables*/
    private var shouldShowMessage = true
    private var hasAPIBeenCalled = false
    private var isEnterPressed = false
    private val viewModel: EditItemViewModel by activityViewModels()
    private lateinit var progressDialog: Dialog
    private lateinit var barcodeButton: FloatingActionButton
    private lateinit var printerButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No changes needed here
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.edit_item_edit_item_details_fragment,
            container,
            false
        )

        setupUI()
        initObservers()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val bundle = arguments
        val lastFragmentName: String = BundleUtils.getPastFragmentNameFromBundle(bundle)
        if (lastFragmentName == "SearchExpirationDateAndLotNumberFragment") {
            binding.upperDiv.visibility = View.GONE
            shouldShowMessage = false
            bundle?.clear()
        } else {
            shouldShowMessage = true
        }
    }

    override fun onPause() {
        super.onPause()
        shouldShowMessage = false
    }

    private fun setupUI() {
        progressDialog = PopupWindowUtils.getLoadingPopup(requireContext())

        // Initialize barcodeButton
        barcodeButton = binding.barcodeButton
        barcodeButton.imageTintList = ColorStateList.valueOf(Color.WHITE)
        barcodeButton.setOnClickListener {
            val itemInfo = viewModel.currentlyChosenItemForSearch.value!!
            val action =
                EditItemDetailsFragmentDirections.actionEditItemDetailsFragmentToEditBarcodeFragment(
                    itemInfo
                )
            view?.findNavController()?.navigate(action)
        }

        // Initialize printerButton
        printerButton = binding.printerButton
        printerButton.imageTintList = ColorStateList.valueOf(Color.WHITE)
        printerButton.setOnClickListener {
            printLabel()
        }

        // Initially disable New Lot EditText since toggle is off
        binding.newLotEditText.isEnabled = false

        // Existing setupUI logic for the enter button
        binding.enterButton.setOnClickListener {
            isEnterPressed = true
            shouldShowMessage = true //NEW
            val itemNumber = viewModel.currentlyChosenItemForSearch.value!!.itemNumber
            val newExpirationDateStr = binding.NewExpirationDateEditText.text.toString()
            val binNumber = viewModel.currentlyChosenItemForSearch.value!!.binLocation
            val lotNumber = binding.newLotEditText.text.toString()
            val oldLot = viewModel.currentlyChosenItemForSearch.value!!.lotNumber

            // Step 1: Validate date format
            val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
            val isValidDateFormat = DateUtils.isValidDate(newExpirationDateStr)

            // Step 2: Check if date format is valid
            if (isValidDateFormat) {
                // Proceed if the date format is valid
                val newExpirationDate = dateFormat.parse(newExpirationDateStr)

                // Step 3: Check if parsed date is valid
                if (newExpirationDate != null) {
                    //progressDialog.show()
                    val warehouseNO =
                        SharedPreferencesUtils.getWarehouseNumberFromSharedPref(requireContext())
                    viewModel.setWarehouseNOFromSharedPref(warehouseNO)

                    val companyID =
                        SharedPreferencesUtils.getCompanyIDFromSharedPref(requireContext())
                    viewModel.setCompanyIDFromSharedPref(companyID)

                    if (newExpirationDateStr.isNotEmpty()) {
                        val handleYesPressed: () -> Unit = {
                            progressDialog.show()
                            assignExpirationDateToItem(
                                lotNumber,
                                itemNumber,
                                warehouseNO,
                                binNumber,
                                companyID,
                                oldLot,
                                newExpirationDateStr
                            )
                        }
                        val isDateExpired = DateUtils.isDateExpired(
                            newExpirationDateStr,
                            handleYesPressed,
                            requireContext(),
                            requireView()
                        )
                        if (!isDateExpired) {
                            progressDialog.show()
                            assignExpirationDateToItem(
                                lotNumber,
                                itemNumber,
                                warehouseNO,
                                binNumber,
                                companyID,
                                oldLot,
                                newExpirationDateStr
                            )
                        }
                    }
                } else {
                    // If the parsed date is null, display an error message
                    AlerterUtils.startErrorAlerter(
                        requireActivity(),
                        "Invalid date format. Please use MM-DD-YYYY."
                    )
                }
            } else {
                // If the date format is invalid, display an error message
                AlerterUtils.startErrorAlerter(
                    requireActivity(),
                    "Invalid date format. Please use MM-DD-YYYY."
                )
            }
        }

        // Add this line in your setupUI function
        binding.NewExpirationDateEditText.inputType = InputType.TYPE_CLASS_NUMBER
        binding.NewExpirationDateEditText.addTextChangedListener(object : TextWatcher {
            private var previousText: String = ""
            private var lastCursorPosition: Int = 0

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Store the text before any change is applied and the cursor position.
                previousText = s.toString()
                lastCursorPosition = binding.NewExpirationDateEditText.selectionStart
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val currentText = s.toString()
                val deleting = currentText.length < previousText.length

                // Strict limit of 10 characters for input, including dashes.
                if (currentText.length > 10) {
                    // Truncate the input to 10 characters
                    val truncatedText = currentText.substring(0, 10)
                    binding.NewExpirationDateEditText.setText(truncatedText)
                    binding.NewExpirationDateEditText.setSelection(truncatedText.length)
                    return
                }

                if (!deleting) {
                    // Automatically insert dashes as the user types.
                    when (currentText.length) {
                        2, 5 -> if (!previousText.endsWith("-")) {
                            binding.NewExpirationDateEditText.setText("$currentText-")
                            binding.NewExpirationDateEditText.setSelection(currentText.length + 1)
                        }
                    }
                } else {
                    // Remove the last character if it's a dash, maintaining proper date format as the user deletes characters.
                    if ((currentText.length == 2 || currentText.length == 5) && previousText.endsWith(
                            "-"
                        )
                    ) {
                        binding.NewExpirationDateEditText.setText(currentText.dropLast(1))
                        binding.NewExpirationDateEditText.setSelection(binding.NewExpirationDateEditText.text.length)
                    } else if (lastCursorPosition > 1 && previousText[lastCursorPosition - 1] == '-') {//Remove number before the dash using cursor position
                        val newPosition =
                            lastCursorPosition - 2  // Position to remove the number before the dash.
                        val newText = StringBuilder(previousText).apply {
                            deleteCharAt(newPosition)  // Remove the number before the dash.
                        }.toString()
                        binding.NewExpirationDateEditText.setText(newText)
                        binding.NewExpirationDateEditText.setSelection(newPosition)
                    }
                }
            }
        })
    }

    private fun assignExpirationDateToItem(
        lotNumber: String,
        itemNumber: String,
        warehouseNO: Int,
        binNumber: String,
        companyID: String,
        oldLot: String?,
        newExpirationDateStr: String
    ) {
        hasAPIBeenCalled = true
        if (lotNumber.isNotEmpty()) {
            viewModel.assignLotNumber(
                itemNumber,
                warehouseNO,
                binNumber,
                lotNumber,
                companyID,
                oldLot
            )
            viewModel.getItemInfo(itemNumber, binNumber, lotNumber)
        }
        viewModel.assignExpirationDate(
            itemNumber,
            binNumber,
            newExpirationDateStr,
            lotNumber,
            warehouseNO
        )
        viewModel.getItemInfo(itemNumber, binNumber, lotNumber)
    }

    private fun initObservers() {

        viewModel.opSuccess.observe(viewLifecycleOwner) { success ->
            // Always dismiss the progress dialog when the observer is triggered
            progressDialog.dismiss()
            // Ensure the message is fetched fresh when needed
            val message = viewModel.opMessage.value ?: "No message available"

            if (isEnterPressed && shouldShowMessage) {
                if (success) {
                    // If the operation was successful, show a success alert
                    AlerterUtils.startSuccessAlert(
                        requireActivity(),
                        "Success!",
                        "Item information changed."
                    )
                } else if (!success) {
                    // If the operation failed, show an error alert
                    AlerterUtils.startErrorAlerter(requireActivity(), message)
                }

                // After handling, reset the enter button state
                isEnterPressed = false
            }
        }

        viewModel.itemInfo.observe(viewLifecycleOwner) { itemInfo ->
            if (itemInfo != null) {
                // Update UI
                binding.itemNumberTextView.text = itemInfo.itemNumber
                binding.itemNameTextView.text = itemInfo.itemDescription
                binding.binLocationTextView.text = itemInfo.binLocation
                binding.expirationDateTextView.text = itemInfo.expireDate ?: "N/A"
                binding.lotTextView.text = itemInfo.lotNumber ?: "N/A"
            }
        }

        // Inside observeViewModel function, modify the observer for currentlyChosenItemForSearch
        viewModel.currentlyChosenItemForSearch.observe(viewLifecycleOwner) { selectedItem ->
            selectedItem?.let { item ->
                binding.apply {
                    itemNumberTextView.text = item.itemNumber
                    itemNameTextView.text = item.itemDescription
                    binLocationTextView.text = item.binLocation
                    expirationDateTextView.text = item.expireDate ?: "N/A"
                    lotTextView.text = item.lotNumber ?: "N/A"

                    // Parsing and formatting the date
                    val inputFormat = SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ) // Adjust this format to match the incoming date format
                    val outputFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
                    val date: Date? = item.expireDate?.let { inputFormat.parse(it) }
                    val formattedDate = if (date != null) outputFormat.format(date) else ""

                    NewExpirationDateEditText.setText(formattedDate)
                    newLotEditText.setText(item.lotNumber ?: "")

                    upperDiv.visibility = View.VISIBLE

                    val lotExists = !item.lotNumber.isNullOrEmpty()
                    newLotEditText.isEnabled = lotExists
                    upperDiv.visibility = View.VISIBLE
                }
            }
        }

        viewModel.wasLastAPICallSuccessful.observe(viewLifecycleOwner) { wasLasAPICallSuccessful ->
            progressDialog.dismiss()
            if (!wasLasAPICallSuccessful && hasAPIBeenCalled) {
                AlerterUtils.startNetworkErrorAlert(
                    requireActivity(),
                    viewModel.networkErrorMessage.value!!
                )
            }
        }
    }

    // New method to print the label
    private fun printLabel() {
        try {
            // Collect data from UI
            val itemNumber = binding.itemNumberTextView.text.toString()
            val itemName = binding.itemNameTextView.text.toString()
            val binLocation = binding.binLocationTextView.text.toString()
            val expirationDate = binding.NewExpirationDateEditText.text.toString()
            val lotNumber = binding.newLotEditText.text.toString()

            // Generate the label bitmap
            val labelBitmap = generateLabelBitmap(
                itemNumber,
                itemName,
                binLocation,
                expirationDate,
                lotNumber
            )

            // Print the label
            printBitmap(labelBitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Printing failed: ${e.message}", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun generateLabelBitmap(
        itemNumber: String,
        itemName: String,
        binLocation: String,
        expirationDate: String,
        lotNumber: String
    ): Bitmap {
        val width = 600
        val height = 800
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 40f
        }

        var yPosition = 50f

        canvas.drawText("Item Number: $itemNumber", 50f, yPosition, paint)
        yPosition += 60f

        canvas.drawText("Item Name: $itemName", 50f, yPosition, paint)
        yPosition += 60f

        canvas.drawText("Bin Location: $binLocation", 50f, yPosition, paint)
        yPosition += 60f

        if (expirationDate.isNotEmpty()) {
            canvas.drawText("Expiration Date: $expirationDate", 50f, yPosition, paint)
            yPosition += 60f
        }

        if (lotNumber.isNotEmpty()) {
            canvas.drawText("Lot Number: $lotNumber", 50f, yPosition, paint)
            yPosition += 60f
        }

        // Generate and draw barcode for the item number
        val barcodeBitmap = generateBarcodeBitmap(itemNumber)
        barcodeBitmap?.let {
            yPosition += 20f
            canvas.drawBitmap(it, 50f, yPosition, null)
        }

        return bitmap
    }

    private fun generateBarcodeBitmap(data: String): Bitmap? {
        return try {
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.encodeBitmap(data, BarcodeFormat.CODE_128, 500, 200)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun printBitmap(bitmap: Bitmap) {
        val printHelper = PrintHelper(requireContext())
        printHelper.scaleMode = PrintHelper.SCALE_MODE_FIT
        printHelper.printBitmap("Pallet Label", bitmap)
    }
}
