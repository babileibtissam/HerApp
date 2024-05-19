package com.example.herapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var emailTextField: EditText
    private lateinit var resetPassword:Button
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        emailTextField = findViewById(R.id.emailTextField)
        resetPassword = findViewById(R.id.resetPassword)
        // backbutton
        val backButton: Button = findViewById(R.id.backbutton)
        // Set click listener for the back button
        backButton.setOnClickListener {
            // Finish the current activity
            finish()
        }
        auth = FirebaseAuth.getInstance()
        resetPassword.setOnClickListener{
            val sPassword = emailTextField.text.toString()
            auth.sendPasswordResetEmail(sPassword)
                .addOnSuccessListener {
                    Toast.makeText(this, "Please Check your Email!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                }
        }
        // Find the TextView by its ID
        val loginlink: TextView = findViewById(R.id.loginlink)

        // Set click listener for the guest link TextView
        loginlink.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}