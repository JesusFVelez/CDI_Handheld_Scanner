package com.example.cdihandheldscannerviewactivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import com.example.cdihandheldscannerviewactivity.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

//TODO(1) falta añadirle animations a cuando se cambia de fragment

    // TODO(2) falta anñadirle el Burger Menu al home screen for visual purposes (despues se le dara funcionalidad un poco mas pensada

private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.custom_toolbar)
        setSupportActionBar(toolbar)

        val navController = this.findNavController(R.id.my_nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (this@MainActivity.findNavController(R.id.my_nav_host_fragment).currentDestination?.id == R.id.homeScreenFragment) {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Log Out")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes") { _, _ ->
                            finish()
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.my_nav_host_fragment)
        return navController.navigateUp()
    }

}