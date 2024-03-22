package com.javierprado.jmapp.view.fragments

import Calificacion
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IngresarCalificacionesFragment : DialogFragment() {

    private lateinit var txtPrimeraNota: EditText
    private lateinit var txtSegundaNota: EditText
    private lateinit var txtTerceraNota: EditText
    private lateinit var txtCuartaNota: EditText
    private lateinit var txtNotaFinal: EditText
    private lateinit var btnIngresarCalificaciones: Button

    private val retro = RetrofitHelper.getInstanceStatic()
    private val colegioAPI = retro.getApi()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ingresar_calificaciones, container, false)

        txtPrimeraNota = view.findViewById(R.id.txt_primera_nota)
        txtSegundaNota = view.findViewById(R.id.txt_segunda_nota)
        txtTerceraNota = view.findViewById(R.id.txt_tercera_nota)
        txtCuartaNota = view.findViewById(R.id.txt_cuarta_nota)
        txtNotaFinal = view.findViewById(R.id.txt_nota_final)
        btnIngresarCalificaciones = view.findViewById(R.id.fg_btn_ingresarc)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnIngresarCalificaciones.setOnClickListener {
            val primeraNota = txtPrimeraNota.text.toString().toIntOrNull()
            val segundaNota = txtSegundaNota.text.toString().toIntOrNull()
            val terceraNota = txtTerceraNota.text.toString().toIntOrNull()
            val cuartaNota = txtCuartaNota.text.toString().toIntOrNull()
            val notaFinal = txtNotaFinal.text.toString().toDoubleOrNull()

            // Verifica si alguna de las notas es nula o si la nota final es nula
            if (primeraNota == null || segundaNota == null || terceraNota == null || cuartaNota == null || notaFinal == null) {
                showToast("Por favor, ingrese todas las notas correctamente.")
                return@setOnClickListener
            }

            // Crea una instancia de Calificacion con las notas ingresadas
            val calificacion = Calificacion().apply {
                calificacion1 = primeraNota
                calificacion2 = segundaNota
                calificacion3 = terceraNota
                calificacion4 = cuartaNota
                calificacionFinal = notaFinal
            }

            // Realiza la llamada al método de la API para ingresar las calificaciones
            colegioAPI.ingresarCalificaciones(calificacion).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // Calificaciones ingresadas exitosamente
                        showToast("Calificaciones ingresadas exitosamente.")
                    } else {
                        showToast("Error al ingresar calificaciones.")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    showToast("Error de red: ${t.message}")
                }
            })
        }
    }

    private fun showToast(message: String) {
        // Muestra un Toast con el mensaje proporcionado
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
}
