package com.example.kuerimex

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kuerimex.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SalesViewModel by activityViewModels()

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

                    binding.productsRecycler.adapter = ProductAdapter(productos, viewModel) { id ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}