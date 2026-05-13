package com.example.kuerimex

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.kuerimex.databinding.FragmentProductsBinding
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class ProductAdapter(private val products: List<ProductRequest>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(){
    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView
        val productPrice: TextView
        val productCode: TextView
        val productDetails: TextView
        val productStock: TextView
        val productImage: ImageView

        init {
            productName = view.findViewById(R.id.productName)
            productPrice = view.findViewById(R.id.productPrice)
            productCode = view.findViewById(R.id.productCode)
            productDetails = view.findViewById(R.id.productDetails)
            productStock = view.findViewById(R.id.productStock)
            productImage = view.findViewById(R.id.productImage)
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
    }

    override fun getItemCount(): Int = products.size

}

class ProductsFragment : Fragment() {
    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!

    private lateinit var rv: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById(R.id.productsRecycler)
        rv.layoutManager = LinearLayoutManager(requireContext())
        obtenerProductos()

        val createProduct = view.findViewById<Button>(R.id.create_product)

        createProduct.setOnClickListener { _ ->
            val intent = Intent(requireContext(), CreateProduct::class.java)
            startActivity(intent)
        }
    }

    private fun obtenerProductos(){
        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val response = RetrofitInstance.api.obtenerProductos()

                if (response.isSuccessful) {

                    val productos = response.body() ?: emptyList<ProductRequest>()

                    rv.adapter = ProductAdapter(productos)

                }

            } catch (e: Exception) {
                Log.e("API_ERROR", e.toString())
            }
        }
    }
}