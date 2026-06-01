package com.example.kuerimex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import coil.load
import com.example.kuerimex.databinding.DialogViewProductBinding

class ViewProduct : DialogFragment() {

    private var _binding: DialogViewProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CartViewModel by activityViewModels()
    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        product = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(PRODUCT_KEY, Product::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(PRODUCT_KEY)
        } ?: throw IllegalArgumentException("Product cannot be null")
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceBundle: Bundle?): View {
        _binding = DialogViewProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            productName.text = product.nombre
            productSku.text = "SKU: ${product.sku}"
            productPrice.text = "$${product.precio}"
            productStock.text = "${product.stock} unidades"
            productCategory.text = product.categoria
            productDetails.text = product.descripcion

            productImage.load(product.imagen_url) {
                crossfade(true)
                placeholder(R.drawable.outline_person_outline_24)
                error(R.drawable.outline_person_outline_24)
            }

            closeButton.setOnClickListener {
                dismiss()
            }

            binding.sellButton.setOnClickListener {
                viewModel.addToCart(product)
                dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams. MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val PRODUCT_KEY = "product"
        fun newInstance(product: Product): ViewProduct {
            val fragment = ViewProduct()
            fragment.arguments = Bundle().apply {
                putParcelable(PRODUCT_KEY, product)
            }
            return fragment
        }
    }
}