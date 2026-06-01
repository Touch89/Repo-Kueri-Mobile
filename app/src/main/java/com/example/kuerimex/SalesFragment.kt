package com.example.kuerimex

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kuerimex.databinding.FragmentSalesBinding
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SalesAdapter(private val sales: List<PedidoResponse>) : RecyclerView.Adapter<SalesAdapter.SaleViewHolder>() {

    class SaleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val saleId: TextView
        val saleDate: TextView
        val total: TextView
        val saleState: TextView

        init {
            saleId = view.findViewById(R.id.sale_id)
            saleDate = view.findViewById(R.id.sale_date)
            total = view.findViewById(R.id.total)
            saleState = view.findViewById(R.id.sale_state)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): SaleViewHolder {
        val view = LayoutInflater.from(parent.context).
                inflate(R.layout.item_sales, parent, false)
        return SaleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val saleItem = sales[position]

        holder.saleId.text = "ID: ${saleItem.id}"
        holder.saleDate.text = formatearFecha(saleItem.fecha_creación)
        holder.total.text = "$${saleItem.precio_total}"
        holder.saleState.text = "Estado: ${saleItem.estado}"
    }

    override fun getItemCount(): Int = sales.size

    private fun formatearFecha(fechaRaw: String?): String {
        if (fechaRaw.isNullOrBlank()) return "Sin fecha"

        return try {
            val fechaParsed = LocalDateTime.parse(fechaRaw)

            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

            fechaParsed.format(formatter)
        } catch (e: Exception) {
            Log.e("PARSE_ERROR", e.toString())
            fechaRaw
        }
    }
}
class SalesFragment : Fragment() {
    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!

    private lateinit var rv: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById(R.id.salesRecycler)
        rv.layoutManager = LinearLayoutManager(requireContext())
        obtenerVentas()

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

    private fun obtenerVentas(){
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.obtenerVentas()

                if (response.isSuccessful) {
                    val sales = response.body() ?: emptyList<PedidoResponse>()
                    binding.salesRecycler.adapter = SalesAdapter(sales)
                }

            }  catch (e: Exception){
                Log.e("API_ERROR", e.toString())
            }
        }
    }
}