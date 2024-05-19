package com.example.herapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.herapp.adapter.FavoriteItemAdapter
import com.example.herapp.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FavoriteActivity : AppCompatActivity() {
    private lateinit var backButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var favoriteAdapter: FavoriteItemAdapter
    private var favoriteItems: MutableList<ProductModel> = mutableListOf()

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize the back button
        backButton = findViewById(R.id.favoriteBackButton)
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.favoriteListView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        favoriteAdapter = FavoriteItemAdapter(favoriteItems)
        recyclerView.adapter = favoriteAdapter

        // Load favorite items
        loadFavoriteItems()
    }

    private fun loadFavoriteItems() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val favoritesRef =
                FirebaseDatabase.getInstance().getReference("Users").child(userId)
                    .child("Favorites")

            val favoritesListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Clear the existing favorite items
                        favoriteItems.clear()

                        // Iterate through each child node and add it to the favoriteItems list
                        for (favoriteSnapshot in snapshot.children) {
                            val productKey = favoriteSnapshot.key
                            productKey?.let { key ->
                                // Fetch the product details using the key
                                val productRef =
                                    FirebaseDatabase.getInstance().getReference("Product")
                                        .child(key)
                                val productListener = object : ValueEventListener {
                                    override fun onDataChange(productSnapshot: DataSnapshot) {
                                        val product = productSnapshot.getValue(ProductModel::class.java)
                                        product?.let {
                                            // Set the key
                                            it.key = productSnapshot.key
                                            favoriteItems.add(it)
                                            favoriteAdapter.notifyDataSetChanged()
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e(
                                            TAG,
                                            "Error loading product details for favorite item: $key",
                                            error.toException()
                                        )
                                    }
                                }
                                // Add the listener to the product reference
                                productRef.addListenerForSingleValueEvent(productListener)
                                // Keep track of the listener to remove it later
                                productRef.removeEventListener(productListener)
                            }
                        }
                    } else {
                        Log.d(TAG, "No favorite items found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error loading favorite items", error.toException())
                }
            }
            // Add the listener to the favorites reference
            favoritesRef.addListenerForSingleValueEvent(favoritesListener)
            // Keep track of the listener to remove it later
            favoritesRef.removeEventListener(favoritesListener)
        } else {
            Log.e(TAG, "User not authenticated")
        }
    }

    companion object {
        private const val TAG = "FavoriteActivity"
    }
}
