package com.example.herapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.herapp.R
import com.example.herapp.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class CartItemAdapter(private val cartItems: MutableList<ProductModel>) : RecyclerView.Adapter<CartItemAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        val productImageView: ImageView = itemView.findViewById(R.id.productImageView)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var userId: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = cartItems[position]

        holder.productNameTextView.text = currentItem.name

        // Parse the price string to a double, handling non-numeric characters
        val price = currentItem.price?.let { parsePrice(it) }
        holder.productPriceTextView.text = "Price: $price"

        // Load image using Picasso library
        Picasso.get()
            .load(currentItem.image)
            .into(holder.productImageView)

        // Set click listener for the delete button
        holder.deleteButton.setOnClickListener {
            val productKey = currentItem.key

            if (productKey != null) {
                // Ensure user is authenticated
                auth = FirebaseAuth.getInstance()
                val currentUser = auth.currentUser

                if (currentUser != null) {
                    // Reference the user's cart in the database
                    userId = currentUser.uid
                    databaseRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Cart")

                    // Remove the item from the database
                    databaseRef.child(productKey).removeValue()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Remove the item from the list
                                cartItems.removeAt(holder.adapterPosition)
                                // Notify adapter of the change
                                notifyDataSetChanged()
                            } else {
                                // Handle error
                                // Log.e(TAG, "Error removing item from cart", task.exception)
                            }
                        }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    // Function to parse price string to double, handling non-numeric characters
    private fun parsePrice(price: String): Double {
        // Remove any non-numeric characters from the price string
        val cleanedPrice = price.replace(Regex("[^0-9.]"), "")
        // Parse the cleaned string to double
        return cleanedPrice.toDoubleOrNull() ?: 0.0
    }
}
