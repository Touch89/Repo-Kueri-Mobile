package com.example.kuerimex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load

class ProductAdapter(private val products: List<Product>,
                     private val viewModel: SalesViewModel,
                     private val onProductClick: (Int) -> Unit)
    : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(){

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView
        val productPrice: TextView
        val productCode: TextView
        val productDetails: TextView
        val productStock: TextView
        val productImage: ImageView

        val sellButton: Button

        init {
            productName = view.findViewById(R.id.productName)
            productPrice = view.findViewById(R.id.productPrice)
            productCode = view.findViewById(R.id.productCode)
            productDetails = view.findViewById(R.id.productDetails)
            productStock = view.findViewById(R.id.productStock)
            productImage = view.findViewById(R.id.productImage)
            sellButton = view.findViewById(R.id.sellButton)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.item_producto, parent, false)

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.productName.text = product.nombre
        holder.productPrice.text = "$${product.precio}"
        holder.productCode.text = "SKU: ${product.sku}"
        holder.productDetails.text = product.descripcion
        holder.productStock.text = "${product.stock} unidades"
        holder.productImage.load(product.imagen_url)

        holder.itemView.setOnClickListener {

            onProductClick(product.id)
            val activity = holder.itemView.context as AppCompatActivity
            val dialog = ViewProduct.newInstance(product)

            dialog.show(
                activity.supportFragmentManager,
                "view_product"
            )
        }

        holder.sellButton.setOnClickListener {
            viewModel.addToCart(product)

            Toast.makeText(holder.itemView.context,
                "${product.nombre} agregado al carrito",
                Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = products.size

}