package com.example.kuerimex

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.kuerimex.databinding.FragmentSalesBinding
import kotlinx.coroutines.launch

class CartAdapter(private var cartItems: List<CartItem>, private val viewModel: CartViewModel)
    : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView
        val productCode: TextView
        val productSubtotal: TextView
        val productQuantity: TextView
        val productImage: ImageView
        val addProduct: Button
        val removeProduct: Button

        init {
            productName = view.findViewById(R.id.product_name)
            productCode = view.findViewById(R.id.product_code)
            productSubtotal = view.findViewById(R.id.product_price)
            productQuantity = view.findViewById(R.id.product_quantity)
            productImage = view.findViewById(R.id.product_image)
            addProduct = view.findViewById(R.id.add_button)
            removeProduct = view.findViewById(R.id.minus_button)
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
        holder.productSubtotal.text = "$${cartItem.product.precio * cartItem.quantity}"
        holder.productQuantity.text = cartItem.quantity.toString()
        holder.productImage.load(cartItem.product.imagen_url)

        holder.addProduct.setOnClickListener {
            viewModel.increaseQuantity(cartItem.product.id)
        }

        holder.removeProduct.setOnClickListener {
            if (cartItem.quantity > 1) {
                viewModel.decreaseQuantity(cartItem.product.id)
            } else {
                androidx.appcompat.app.AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Eliminar producto")
                    .setMessage("¿Estás seguro de que deseas eliminar este producto del carrito?")
                    .setPositiveButton("Confirmar") { _, _ ->
                        viewModel.removeItem(cartItem.product.id)
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateItems(newItems: List<CartItem>) {
        this.cartItems = newItems
        notifyDataSetChanged()
    }
}

class CartFragment : Fragment() {
    private val viewModel: CartViewModel by activityViewModels()
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
        val adapter = CartAdapter(emptyList(), viewModel)
        binding.salesRecycler.adapter = adapter

        // Manejar los cambios en el carrito
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cartItems.collect { cartItems ->
                adapter.updateItems(cartItems)
                binding.totalCart.text = "Total: $${viewModel.calculateTotal()}"
            }
        }

        binding.btnFinalizarVenta.setOnClickListener {
            if (viewModel.cartItems.value.isNotEmpty()) {
                enviarVenta()
            } else {
                Toast.makeText(requireContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->val insets = windowInsets.getInsets(
            WindowInsetsCompat.Type.systemBars())
            view.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun enviarVenta() {
        val productsApi = viewModel.cartItems.value.map { cartItem ->
            ProductoEnPedido(cartItem.product.id, cartItem.quantity)
        }

        val pedido = Pedido(productsApi)

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val response = RetrofitInstance.api.crearPedidoFisico(pedido)

                if (response.isSuccessful) {

                    val pedidoCreado = response.body()
                    Toast.makeText(requireContext(),
                        "Venta realizada con éxito",
                        Toast.LENGTH_SHORT).show()

                    viewModel.clearCart()
                } else {
                    val error = response.errorBody()?.string() ?: "Error desconocido"
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("API_ERROR", e.toString())
            }
        }
    }
}
