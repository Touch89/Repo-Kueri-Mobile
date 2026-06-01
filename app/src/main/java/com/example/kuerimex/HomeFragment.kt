package com.example.kuerimex

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kuerimex.databinding.FragmentHomeBinding
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CartViewModel by activityViewModels()

    private var listProducts: List<Product> = emptyList()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.productsRecycler.layoutManager = LinearLayoutManager(requireContext())
        obtenerProductos()
        configurarBuscador()

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

                    listProducts = response.body() ?: emptyList<Product>()

                    adapter = ProductAdapter(listProducts, viewModel) { id ->
                        obtenerProducto(id)
                    }
                    binding.productsRecycler.adapter = adapter

                    generarBotonesCategorias()
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

    private fun configurarBuscador(){
        binding.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(searchText: String?): Boolean {
                filtrarProductos(searchText)
                return true
            }
        })
    }

    private fun filtrarProductos(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            listProducts
        } else {
            listProducts.filter { product ->
                product.nombre.contains(query, ignoreCase = true) ||
                        product.sku.contains(query, ignoreCase = true)
            }
        }

        adapter.updateList(filteredList)
    }

    private fun generarBotonesCategorias(){
        binding.categoryFilter.removeAllViews()

        // boton para todos los productos
        val categorias = mutableListOf("todos")
        categorias.addAll(listProducts.map { it.categoria }.distinct().sorted())

        // generar botones para cada categoria
        for (categoria in categorias){
            val contextThemeWrapper = android.view.ContextThemeWrapper(requireContext(), R.style.ButtonBlack)
            val button = MaterialButton(contextThemeWrapper)

            button.text = categoria
            button.setAllCaps(false)

            // margenes
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 16, 0)
            button.layoutParams = params

            button.setOnClickListener {
                filtrarPorCategorias(categoria)
            }

            binding.categoryFilter.addView(button)
        }
    }

    private fun filtrarPorCategorias(categoria: String){
        val filteredList = if (categoria == "todos") {
            listProducts
        } else {
            listProducts.filter { it.categoria == categoria }
        }
        adapter.updateList(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}