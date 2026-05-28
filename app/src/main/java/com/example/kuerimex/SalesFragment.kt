package com.example.kuerimex

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.kuerimex.databinding.FragmentSalesBinding
import kotlinx.coroutines.launch

class CartAdapter(private var cartItems: List<CartItem>)
    : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView
        val productCode: TextView
        val productSubtotal: TextView
        val productQuantity: TextView
        val productImage: ImageView

        init {
            productName = view.findViewById(R.id.product_name)
            productCode = view.findViewById(R.id.product_code)
            productSubtotal = view.findViewById(R.id.product_price)
            productQuantity = view.findViewById(R.id.product_quantity)
            productImage = view.findViewById(R.id.product_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): CartViewHolder {
        val view = LayoutInflater.from(parent.context).
                inflate(R.layout.item_sales, parent, false)

        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]

        holder.productName.text = cartItem.product.nombre
        holder.productCode.text = "SKU: ${cartItem.product.sku}"
        holder.productSubtotal.text = "Subtotal: $${cartItem.product.precio * cartItem.quantity}"
        holder.productQuantity.text = cartItem.quantity.toString()
        holder.productImage.load(cartItem.product.imagen_url)
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateItems(newItems: List<CartItem>) {
        this.cartItems = newItems
        notifyDataSetChanged()
    }
}

class SalesFragment : Fragment() {
    private val viewModel: SalesViewModel by viewModels()
    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.salesRecycler.layoutManager = LinearLayoutManager(requireContext())

        // Configurar el RecyclerView
        val adapter = CartAdapter(emptyList())
        binding.salesRecycler.adapter = adapter

        // Manejar los cambios en el carrito
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cartItems.collect { cartItems ->
                adapter.updateItems(cartItems)
                binding.totalCart.text = "Total: $${viewModel.calculateTotal()}"
            }
        }

        binding.btnFinalizarVenta.setOnClickListener {
            enviarVenta()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun enviarVenta() {
        val request = SalesRequest(
            items = viewModel.cartItems.value.map { CartItem(it.product, it.quantity) },
            total = viewModel.calculateTotal()
        )
    }
}