package com.example.herapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.herapp.R
import com.example.herapp.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ProductActivity : AppCompatActivity() {
    private lateinit var productNameTextView: TextView
    private lateinit var productDescriptionTextView: TextView
    private lateinit var productPriceTextView: TextView
    private lateinit var productImageView: ImageView
    private lateinit var ajouterPanierButton: Button
    private lateinit var addToFavoriteButton: ImageView
    private lateinit var auth: FirebaseAuth
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        auth = FirebaseAuth.getInstance()

        productNameTextView = findViewById(R.id.productName)
        productDescriptionTextView = findViewById(R.id.productDescription)
        productPriceTextView = findViewById(R.id.productPrice)
        productImageView = findViewById(R.id.productImage)
        ajouterPanierButton = findViewById(R.id.ajouterPanierButton)
        addToFavoriteButton = findViewById(R.id.ajouterFavoriteButton)

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val productKey = intent.getStringExtra("PRODUCT_KEY")

        loadProductDetails(productKey)

        ajouterPanierButton.setOnClickListener {
            addProductToCart(productKey)
        }

        // Handle adding/removing from favorites when addToFavoriteButton is clicked
        addToFavoriteButton.setOnClickListener {
            toggleFavorite(productKey)
        }
    }

    private fun loadProductDetails(productKey: String?) {
        productKey?.let {
            val databaseRef = FirebaseDatabase.getInstance().getReference("Product").child(productKey)

            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val product = snapshot.getValue(ProductModel::class.java)
                    product?.let {
                        productNameTextView.text = it.name
                        productDescriptionTextView.text = it.description
                        productPriceTextView.text = it.price

                        Picasso.get()
                            .load(it.image)
                            .into(productImageView)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error loading product details", error.toException())
                }
            })
        }
    }

    private fun addProductToCart(productKey: String?) {
        val currentUser = auth.currentUser
        if (currentUser != null && productKey != null) {
            val userCartRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.uid).child("Cart")
            userCartRef.child(productKey).setValue(true)
            navigateToCartActivity()
        } else {
            Log.e(TAG, "User not authenticated or product key is null")
        }
    }

    private fun toggleFavorite(productKey: String?) {
        val currentUser = auth.currentUser
        if (currentUser != null && productKey != null) {
            val userFavoritesRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.uid).child("Favorites")

            userFavoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(productKey)) {
                        // Already in favorites, so remove it
                        userFavoritesRef.child(productKey).removeValue()
                        isFavorite = false
                        // Change the button icon
                        addToFavoriteButton.setImageResource(R.drawable.favorite)
                    } else {
                        // Not in favorites, add it
                        userFavoritesRef.child(productKey).setValue(true)
                        isFavorite = true
                        // Change the button icon
                        addToFavoriteButton.setImageResource(R.drawable.favoriteadded)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error toggling favorite", error.toException())
                }
            })
        } else {
            Log.e(TAG, "User not authenticated or product key is null")
        }
    }


    private fun navigateToCartActivity() {
        val intent = Intent(this@ProductActivity, CartActivity::class.java)
        startActivity(intent)
    }
}
