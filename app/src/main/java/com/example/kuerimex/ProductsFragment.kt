package com.example.kuerimex

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kuerimex.databinding.FragmentProductsBinding
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

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

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->val insets = windowInsets.getInsets(
            WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
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