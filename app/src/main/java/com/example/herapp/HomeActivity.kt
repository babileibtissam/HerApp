package com.example.herapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.herapp.adapter.MyProductAdapter
import com.example.herapp.model.ProductModel
import com.google.firebase.database.*

class HomeActivity : AppCompatActivity() {
    private lateinit var mainLayout: RecyclerView
    private lateinit var backButton: Button
    private lateinit var favButton: Button
    private lateinit var cartButton: Button
    private lateinit var searchView: SearchView
    private lateinit var productAdapter: MyProductAdapter
    private var originalProductList: MutableList<ProductModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mainLayout = findViewById(R.id.recyclerProducts)
        backButton = findViewById(R.id.backbutton)
        favButton = findViewById(R.id.favbutton)
        cartButton = findViewById(R.id.cartButton)
        searchView = findViewById(R.id.searchView)

        mainLayout.layoutManager = GridLayoutManager(this, 2)

        // Initialize adapter
        productAdapter = MyProductAdapter { productKey ->
            val intent = Intent(this@HomeActivity, ProductActivity::class.java)
            intent.putExtra("PRODUCT_KEY", productKey)
            startActivity(intent)
        }
        mainLayout.adapter = productAdapter

        loadProducts()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    // If search query is empty, load all products
                    productAdapter.submitList(originalProductList)
                } else {
                    // Filter the list based on the search query
                    productAdapter.filterList(newText.orEmpty())
                }
                return true
            }
        })

        backButton.setOnClickListener {
            finish()
        }

        favButton.setOnClickListener {
            val intent = Intent(this, FavoriteActivity::class.java)
            startActivity(intent)
        }

        cartButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadProducts() {
        val databaseRef = FirebaseDatabase.getInstance().getReference("Product")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    originalProductList.clear()
                    for (productSnapshot in snapshot.children) {
                        val productModel = productSnapshot.getValue(ProductModel::class.java)
                        productModel?.let {
                            it.key = productSnapshot.key
                            originalProductList.add(it)
                        }
                    }
                    productAdapter.submitList(originalProductList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
