package com.example.herapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.herapp.R
import com.example.herapp.model.ProductModel
import com.squareup.picasso.Picasso

class MyProductAdapter(
    private val onItemClick: (String) -> Unit // Define a function type for item click
) : ListAdapter<ProductModel, MyProductAdapter.MyProductViewHolder>(ProductModelComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProductViewHolder {
        // Inflate the layout for each item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return MyProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyProductViewHolder, position: Int) {
        // Get the data at the position
        val product = getItem(position)
        // Bind the data with the view holder
        holder.bind(product)
    }

    inner class MyProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Define views in the view holder
        private val cardView: CardView = itemView.findViewById(R.id.cardView)
        private val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        private val priceTextView: TextView = itemView.findViewById(R.id.textPrice)
        private val productImageView: ImageView = itemView.findViewById(R.id.imageView)

        // Function to bind the data to the view
        fun bind(product: ProductModel) {
            // Set data to the views
            productNameTextView.text = product.name
            priceTextView.text = product.price

            // Load image into ImageView using Picasso
            product.image?.let { imageUrl ->
                Picasso.get().load(imageUrl).into(productImageView)
            }

            // Set OnClickListener to the card
            cardView.setOnClickListener {
                // Call onItemClick function with product key when the card is clicked
                onItemClick(product.key ?: "")
            }
        }
    }

    // Comparator to compare product items
    class ProductModelComparator : DiffUtil.ItemCallback<ProductModel>() {
        override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem == newItem
        }
    }

    // Function to filter the product list based on a search query
    fun filterList(text: String) {
        val filteredList = if (text.isBlank()) {
            currentList
        } else {
            currentList.filter { it.name?.contains(text, ignoreCase = true) == true }
        }
        submitList(filteredList)
    }
}
