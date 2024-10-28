package com.scannerapp.cdihandheldscannerviewactivity.EditItem.EditItemInformationPage

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import java.io.OutputStream
import java.net.Socket
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
            // Show a dialog to let the user choose between printing bin label or item label
            val options = arrayOf("Print Bin Label", "Print Item Label")
            AlertDialog.Builder(requireContext())
                .setTitle("Choose Label to Print")
                .setItems(options) { dialog, which ->
                    when (which) {
                        0 -> { // Print Bin Label
                            printBinLabel()
                        }
                        1 -> { // Print Item Label
                            printItemLabel()
                        }
                    }
                }
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Printing failed: ${e.message}", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun printBinLabel() {
        try {
            // Collect bin data from UI
            val binLocation = binding.binLocationTextView.text.toString()

            // Generate the bin label bitmap
            val labelBitmap = generateBinLabelBitmap(binLocation)

            // Print the label
            printBitmap(labelBitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Printing failed: ${e.message}", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun printItemLabel() {
        try {
            // Collect data from UI
            val itemNumber = binding.itemNumberTextView.text.toString()
            val itemName = binding.itemNameTextView.text.toString()
            val binLocation = binding.binLocationTextView.text.toString()
            val expirationDate = binding.NewExpirationDateEditText.text.toString()
            val lotNumber = binding.newLotEditText.text.toString()

            // Generate the label bitmap
            val labelBitmap = generateItemLabelBitmap(
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

    private fun generateItemLabelBitmap(
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

        // Set textAlign to LEFT and manually center text
        val paint = TextPaint().apply {
            color = Color.BLACK
            textSize = 40f
            textAlign = Paint.Align.LEFT
        }

        var yPosition = 50f

        // Draw Item Number
        val itemNumberText = "Item Number: $itemNumber"
        val itemNumberWidth = paint.measureText(itemNumberText)
        val itemNumberX = (width - itemNumberWidth) / 2f
        canvas.drawText(itemNumberText, itemNumberX, yPosition, paint)
        yPosition += 60f

        // Draw Item Name (handle multi-line text)
        val itemNameText = "Item Name: $itemName"
        val maxTextWidth = width - 100 // 50px padding on each side
        val itemNameLayout = createStaticLayout(itemNameText, paint, maxTextWidth)
        canvas.save()
        canvas.translate(50f, yPosition) // Start drawing at x=50f to center text within padded area
        itemNameLayout.draw(canvas)
        canvas.restore()
        yPosition += itemNameLayout.height + 20f

        // Draw Bin Location
        val binLocationText = "Bin Location: $binLocation"
        val binLocationWidth = paint.measureText(binLocationText)
        val binLocationX = (width - binLocationWidth) / 2f
        canvas.drawText(binLocationText, binLocationX, yPosition, paint)
        yPosition += 60f

        // Draw Expiration Date if available
        if (expirationDate.isNotEmpty()) {
            val expirationDateText = "Expiration Date: $expirationDate"
            val expirationDateWidth = paint.measureText(expirationDateText)
            val expirationDateX = (width - expirationDateWidth) / 2f
            canvas.drawText(expirationDateText, expirationDateX, yPosition, paint)
            yPosition += 60f
        }

        // Draw Lot Number if available
        if (lotNumber.isNotEmpty()) {
            val lotNumberText = "Lot Number: $lotNumber"
            val lotNumberWidth = paint.measureText(lotNumberText)
            val lotNumberX = (width - lotNumberWidth) / 2f
            canvas.drawText(lotNumberText, lotNumberX, yPosition, paint)
            yPosition += 60f
        }

        // Generate and draw barcode for the item number
        val barcodeBitmap = generateBarcodeBitmap(itemNumber)
        barcodeBitmap?.let { bitmap ->
            yPosition += 20f
            val barcodeX = (width - bitmap.width) / 2f // Center the barcode
            canvas.drawBitmap(bitmap, barcodeX, yPosition, null)
        }

        return bitmap
    }


    private fun generateBinLabelBitmap(binLocation: String): Bitmap {
        val width = 600
        val height = 400 // Adjust the height as needed
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = TextPaint().apply {
            color = Color.BLACK
            textSize = 60f // Larger text size for bin label
            textAlign = Paint.Align.CENTER // Center the text horizontally
        }

        val centerX = width / 2f // Center position on the X-axis
        var yPosition = 100f

        // Draw bin location text (without "Bin Location:")
        canvas.drawText(binLocation, centerX, yPosition, paint)
        yPosition += 80f

        // Generate and draw barcode for the bin location
        val barcodeBitmap = generateBarcodeBitmap(binLocation)
        barcodeBitmap?.let { bitmap ->
            yPosition += 20f
            val barcodeX = (width - bitmap.width) / 2f // Center the barcode
            canvas.drawBitmap(bitmap, barcodeX, yPosition, null)
        }

        return bitmap
    }

    private fun createStaticLayout(text: String, paint: TextPaint, width: Int): StaticLayout {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
                .setAlignment(Layout.Alignment.ALIGN_CENTER) // Center text within the StaticLayout
                .setLineSpacing(0f, 1f)
                .setIncludePad(false)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(
                text, paint, width,
                Layout.Alignment.ALIGN_CENTER, 1f, 0f, false
            )
        }
    }

    private fun generateZPLLabel(
        itemNumber: String,
        itemName: String,
        binLocation: String,
        expirationDate: String,
        lotNumber: String
    ): String {
        return """
        ^XA
        ^FO100,50^A0N,50,50^FDItem Number: $itemNumber^FS
        ^FO100,120^A0N,50,50^FDName: $itemName^FS
        ^FO100,190^A0N,50,50^FDBin Location: $binLocation^FS
        ${if (expirationDate.isNotEmpty()) "^FO100,260^A0N,50,50^FDExpiration Date: $expirationDate^FS" else ""}
        ${if (lotNumber.isNotEmpty()) "^FO100,330^A0N,50,50^FDLot Number: $lotNumber^FS" else ""}
        ^FO100,400^BY2^BCN,100,Y,N,N^FD$itemNumber^FS
        ^XZ
    """.trimIndent()
    }

    private fun generateDPLLabel(
        itemNumber: String,
        itemName: String,
        binLocation: String,
        expirationDate: String,
        lotNumber: String
    ): String {
        return """
        {D0300,0300,0700|}                     // Define label size and positioning
        {C|}                                   // Clear the label format
        {PC000;0030,0040,0,4,1,1,$itemNumber|} // Item Number
        {PC001;0030,0080,0,4,1,1,$itemName|}   // Name
        {PC002;0030,0120,0,4,1,1,$binLocation|}// Bin Location
        ${if (expirationDate.isNotEmpty()) "{PC003;0030,0160,0,4,1,1,Expiration Date: $expirationDate|}" else ""}
        ${if (lotNumber.isNotEmpty()) "{PC004;0030,0200,0,4,1,1,Lot Number: $lotNumber|}" else ""}
        {XB00;0040,0240,T,H,0,9,0,2,$itemNumber|}  // Barcode for item number
        {XS;I,0001,0002C6110|}                 // Print and cut the label
    """.trimIndent()
    }

    private fun sendCommandToPrinter(printerIp: String, printerPort: Int, command: String) {
        try {
            val socket = Socket(printerIp, printerPort)
            val outputStream: OutputStream = socket.getOutputStream()
            outputStream.write(command.toByteArray())
            outputStream.flush()
            outputStream.close()
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to send command to printer: ${e.message}", Toast.LENGTH_LONG).show()
        }
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
