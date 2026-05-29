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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.kuerimex.databinding.FragmentProductsBinding
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

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

class ProductsFragment : Fragment() {
    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SalesViewModel by activityViewModels()

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

                    val productos = response.body() ?: emptyList<Product>()

                    rv.adapter = ProductAdapter(productos, viewModel) { id ->
                        obtenerProducto(id)
                    }

                }

            } catch (e: Exception) {
                Log.e("API_ERROR", e.toString())
            }
        }
    }

    private fun obtenerProducto(id: Int){
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.obtenerProducto(id)

                if (response.isSuccessful) {

                    val producto = response.body()

                    if (producto != null) {

                        val dialog =
                            ViewProduct.newInstance(producto)

                        dialog.show(
                            parentFragmentManager,
                            "view_product"
                        )
                    }
                }

            } catch (e: Exception) {
                Log.e("API_ERROR", e.toString())
            }
        }
    }


}