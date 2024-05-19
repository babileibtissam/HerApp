package com.example.herapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ClientInfoActivity : AppCompatActivity() {
    private lateinit var backButton: Button
    private lateinit var nameTextField: EditText
    private lateinit var numberTextField: EditText
    private lateinit var cityTextField: EditText
    private lateinit var addressTextField: EditText
    private lateinit var doneButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_info)

        // Initialize views
        nameTextField = findViewById(R.id.nameTextField)
        numberTextField = findViewById(R.id.numberTextField)
        cityTextField = findViewById(R.id.cityTextField)
        addressTextField = findViewById(R.id.addressTextField)
        doneButton = findViewById(R.id.doneButton)

        // Initialize the back button
        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Set OnClickListener for the Done button
        doneButton.setOnClickListener {
            // Get text from EditText fields
            val name = nameTextField.text.toString().trim()
            val number = numberTextField.text.toString().trim()
            val city = cityTextField.text.toString().trim()
            val address = addressTextField.text.toString().trim()

            // Check if any field is empty
            if (name.isEmpty() || number.isEmpty() || city.isEmpty() || address.isEmpty()) {
                // Show a toast or error message indicating that all fields are required

                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                // Create an intent to pass the client information to the next activity
                val intent = Intent(this, DoneActivity::class.java)
                intent.putExtra("NAME", name)
                intent.putExtra("NUMBER", number)
                intent.putExtra("CITY", city)
                intent.putExtra("ADDRESS", address)
                startActivity(intent)
            }
        }
    }
}