package com.example.herapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.herapp.adapter.CartItemAdapter
import com.example.herapp.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartActivity : AppCompatActivity() {
    private lateinit var backButton: Button
    private lateinit var checkoutButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartItemAdapter
    private var cartItems: MutableList<ProductModel> = mutableListOf()
    private lateinit var databaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    // TextView to display the total price
    private lateinit var totalView: TextView
    private var totalPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize the back button
        backButton = findViewById(R.id.cartBackButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
        checkoutButton = findViewById(R.id.checkoutButton)
        checkoutButton.setOnClickListener {
            val intent = Intent(this, ClientInfoActivity::class.java)
            startActivity(intent)
        }

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.cartRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartItemAdapter(cartItems)
        recyclerView.adapter = cartAdapter

        // Initialize the total view
        totalView = findViewById(R.id.totalView)

        // Load cart items
        loadCartItems()
    }

    private fun loadCartItems() {
        // Adjust the database reference to the appropriate location in your Firebase database
        val cartRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser!!.uid).child("Cart")

        // Add a listener to read the data at the reference
        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Clear the existing cart items
                    cartItems.clear()

                    // Reset total price
                    totalPrice = 0.0

                    // Iterate through each child node and add it to the cartItems list
                    for (cartSnapshot in dataSnapshot.children) {
                        val productKey = cartSnapshot.key
                        productKey?.let { key ->
                            // Assuming "Product" node contains all product details
                            val productRef = FirebaseDatabase.getInstance().getReference("Product").child(key)
                            productRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(productSnapshot: DataSnapshot) {
                                    val product = productSnapshot.getValue(ProductModel::class.java)
                                    product?.let {
                                        // Set the key to the product
                                        it.key = key
                                        // Update total price
                                        val price = it.price?.let { priceString ->
                                            parsePrice(priceString)
                                        } ?: 0.0
                                        totalPrice += price
                                        totalView.text = "Total: $${String.format("%.2f", totalPrice)}"
                                        cartItems.add(it)
                                        cartAdapter.notifyDataSetChanged() // Notify adapter when new item added
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e(TAG, "Error loading product details for cart item", error.toException())
                                }
                            })
                        }
                    }
                } else {
                    Log.d(TAG, "Cart is empty")
                    totalView.text = "Total: $0.00" // Set total to zero if cart is empty
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e(TAG, "Error loading cart items", databaseError.toException())
            }
        })
    }


    // Function to parse price string to double, handling non-numeric characters
    private fun parsePrice(price: String): Double {
        // Remove any non-numeric characters from the price string
        val cleanedPrice = price.replace(Regex("[^0-9.]"), "")
        // Parse the cleaned string to double
        return cleanedPrice.toDoubleOrNull() ?: 0.0
    }

    companion object {
        private const val TAG = "CartActivity"
    }
}
