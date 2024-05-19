package com.example.herapp.adapter

import android.content.ContentValues.TAG
import android.util.Log
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

class FavoriteItemAdapter(private val favoriteItems: MutableList<ProductModel>) :
    RecyclerView.Adapter<FavoriteItemAdapter.FavoriteViewHolder>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        val productImageView: ImageView = itemView.findViewById(R.id.productImageView)
        val deleteButton: ImageButton = itemView.findViewById(R.id.favdeleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite, parent, false)
        return FavoriteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val currentItem = favoriteItems[position]

        holder.productNameTextView.text = currentItem.name
        holder.productPriceTextView.text = "Price: ${currentItem.price}"

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
                    // Reference the user's favorites in the database
                    val userId = currentUser.uid
                    databaseRef =
                        FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Favorites")

                    // Remove the item from the database
                    databaseRef.child(productKey).removeValue()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Remove the item from the list
                                val removedPosition = holder.adapterPosition
                                favoriteItems.removeAt(removedPosition)
                                // Notify adapter of the change
                                notifyItemRemoved(removedPosition)
                            } else {
                                // Handle error
                                Log.e(TAG, "Error removing item from favorites", task.exception)
                            }
                        }
                } else {
                    // User is not authenticated
                    Log.e(TAG, "Current user is null")
                }
            } else {
                // Product key is null
                Log.e(TAG, "Product key is null")
            }
        }
    }


    override fun getItemCount(): Int {
        return favoriteItems.size
    }

    private fun deleteItem(productKey: String, position: Int) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val favoritesRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(userId).child("Favorites")

            favoritesRef.child(productKey).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Remove the item from the list
                        favoriteItems.removeAt(position)
                        // Notify adapter of the change
                        notifyItemRemoved(position)
                    } else {
                        // Handle error
                        // Log.e(TAG, "Error removing item from favorites", task.exception)
                    }
                }
        }
    }
}
