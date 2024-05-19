package com.example.herapp

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.herapp.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth


class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Find the password field by its ID
        val passwordField = binding.passwordtextfield

        // Set click listener for the password visibility toggle icon
        passwordField.setDrawableClickListener { view ->
            // Toggle password visibility
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(passwordField)
        }

        // Initialize password visibility
        togglePasswordVisibility(passwordField)

        // Set click listener for the forgot password TextView
        binding.forgotpassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the back button
        binding.backButton.setOnClickListener {
            // Finish the current activity
            finish()
        }

        // Set click listener for the register link TextView
        binding.registerlink.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Set click listener for the login button
        binding.loginButton.setOnClickListener {
            val email = binding.emailtextfield.text.toString()
            val pass = binding.passwordtextfield.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                // Perform sign in with email and password
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        // Sign in successful, navigate to HomeActivity
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish() // Finish current activity to prevent going back to it
                    } else {
                        // Sign in failed, display error message
                        Toast.makeText(this, "Wrong email or password", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Email or password field is empty, display error message
                Toast.makeText(this, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun togglePasswordVisibility(passwordField: EditText) {
        if (isPasswordVisible) {
            // Show password
            passwordField.transformationMethod = null
            passwordField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visibility, 0)
        } else {
            // Hide password
            passwordField.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
            passwordField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visibility_off, 0)
        }

        // Move cursor to the end of the text
        passwordField.setSelection(passwordField.text.length)
    }

    // Extension function to set a click listener on compound drawables
    private fun EditText.setDrawableClickListener(onClickListener: (view: View) -> Unit) {
        this.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= this.right - this.compoundDrawables[2].bounds.width()) {
                    onClickListener.invoke(this)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }
}