package com.example.kuerimex

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class CreateProduct : AppCompatActivity() {

    // name
    private lateinit var nameLayout: TextInputLayout
    private lateinit var nameInputLayout: TextInputEditText

    // image
    private lateinit var imgLayout: TextInputLayout
    private lateinit var imgInputLayout: TextInputEditText

    // description
    private lateinit var descLayout: TextInputLayout
    private lateinit var descInputLayout: TextInputEditText

    // price
    private lateinit var priceLayout: TextInputLayout
    private lateinit var priceInputLayout: TextInputEditText

    // sku
    private lateinit var skuLayout: TextInputLayout
    private lateinit var skuInputLayout: TextInputEditText

    // send
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_product)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // name
        nameLayout = findViewById(R.id.layout_name)
        nameInputLayout = findViewById(R.id.txtbx_name)

        // img
        imgLayout = findViewById(R.id.layout_img)
        imgInputLayout = findViewById(R.id.txtbx_img)

        // desc
        descLayout = findViewById(R.id.layout_desc)
        descInputLayout = findViewById(R.id.txtbx_desc)

        // price
        priceLayout = findViewById(R.id.layout_price)
        priceInputLayout = findViewById(R.id.txtbx_price)

        // sku
        skuLayout = findViewById(R.id.layout_sku)
        skuInputLayout = findViewById(R.id.txtbx_sku)

        // button
        sendButton = findViewById(R.id.btn_create_product)

        sendButton.setOnClickListener { _ ->
            if (validarCampos()){
                crearProducto()
                finish()
            }
        }
    }

    fun validarCampos(): Boolean {
        var isValid = true

        if (nameInputLayout.text.isNullOrEmpty()){
            nameLayout.error = "Este campo es obligatorio"
            isValid = false
        } else {
            nameLayout.error = null
        }

        if (imgInputLayout.text.isNullOrEmpty()){
            imgLayout.error = "Este campo es obligatorio"
            isValid = false
        } else {
            imgLayout.error = null
        }

        if (descInputLayout.text.isNullOrEmpty()){
            descLayout.error = "Este campo es obligatorio"
            isValid = false
        } else {
            descLayout.error = null
        }

        if (priceInputLayout.text.isNullOrEmpty()){
            priceLayout.error = "Este campo es obligatorio"
            isValid = false
        } else {
            priceLayout.error = null
        }

        if (skuInputLayout.text.isNullOrEmpty()){
            skuLayout.error = "Este campo es obligatorio"
            isValid = false
        } else {
            skuLayout.error = null
        }

        return isValid
    }

    fun crearProducto() {

        val producto = ProductRequest(
            nombre = nameInputLayout.text.toString(),
            descripcion = descInputLayout.text.toString(),
            imagen = imgInputLayout.text.toString(),
            precio = priceInputLayout.text.toString().toFloat(),
            sku = skuInputLayout.text.toString()
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.crearProducto(producto)

                if (response.isSuccessful){
                    Toast.makeText(this@CreateProduct, "Producto creado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CreateProduct, "Error al crear producto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception){
                Toast.makeText(this@CreateProduct, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}