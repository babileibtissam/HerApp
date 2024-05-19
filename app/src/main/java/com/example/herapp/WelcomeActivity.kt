package com.example.herapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Find login and register buttons by their IDs
        val loginButton: Button = findViewById(R.id.loginButton)
        val registerButton: Button = findViewById(R.id.registerButton)

        // Set click listener for the login button
        loginButton.setOnClickListener {
            // Create an Intent to navigate to SignInActivity
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the register button
        registerButton.setOnClickListener {
            // Create an Intent to navigate to SignUpActivity
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Find the TextView by its ID
        val guestLinkText: TextView = findViewById(R.id.guestLinkText) // Replace with your actual ID

        // Set click listener for the guest link TextView
        guestLinkText.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}