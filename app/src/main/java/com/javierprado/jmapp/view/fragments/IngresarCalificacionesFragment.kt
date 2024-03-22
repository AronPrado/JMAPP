package com.javierprado.jmapp.view.fragments

import Calificacion
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IngresarCalificacionesFragment : DialogFragment() {
    private lateinit var txtPrimeraNota: EditText
    private lateinit var txtSegundaNota: EditText
    private lateinit var txtTerceraNota: EditText
    private lateinit var txtCuartaNota: EditText
    private lateinit var txtNotaFinal: EditText
    private lateinit var btnGuardarCalificaciones: Button

    val TOKEN = "token" ; val ESTUDIANTE = "alumno" ; val CURSO = "curso"

    private val retro = RetrofitHelper.getInstanceStatic()
    private var estudianteId: Int = 0
    private var cursoId: Int = 0
    private lateinit var activity: AppCompatActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as ControlSeleccionActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            retro.setBearerToken(it.getString(TOKEN, ""))
            estudianteId = it.getInt(ESTUDIANTE)
            cursoId = it.getInt(CURSO)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ingresar_calificaciones, container, false)
        txtPrimeraNota = view.findViewById(R.id.txt_primera_nota)
        txtSegundaNota = view.findViewById(R.id.txt_segunda_nota)
        txtTerceraNota = view.findViewById(R.id.txt_tercera_nota)
        txtCuartaNota = view.findViewById(R.id.txt_cuarta_nota)
        txtNotaFinal = view.findViewById(R.id.txt_nota_final)
        btnGuardarCalificaciones = view.findViewById(R.id.fg_guardar_cambios)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val api = retro.getApi()


        Log.e("SE BUSCA", "E: ${estudianteId}, C: ${cursoId}")
        api.obtenerCalificaciones(estudianteId, cursoId).enqueue(object : Callback<Calificacion> {
            override fun onResponse(call: Call<Calificacion>, response: Response<Calificacion>) {
                if (response.isSuccessful) {
                    val calificaciones = response.body()
                    val nota1 = calificaciones!!.calificacion1.toString()
                    val nota2 = calificaciones.calificacion1.toString()
                    val nota3 = calificaciones.calificacion1.toString()
                    val nota4 = calificaciones.calificacion1.toString()
                    val notaF = calificaciones.calificacionFinal.toString()

                    txtPrimeraNota.setText(nota1) ; txtSegundaNota.setText(nota2)
                    txtTerceraNota.setText(nota3) ; txtCuartaNota.setText(nota4) ;
                    txtNotaFinal.setText(notaF)
                } else {
                    showToast("No se encontraron calificaciones.")
                }
            }
            override fun onFailure(call: Call<Calificacion>, t: Throwable) {
                showToast("Error de red: ${t.message}")
            }
        })

        btnGuardarCalificaciones.setOnClickListener {
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
            api.ingresarCalificaciones(calificacion).enqueue(object : Callback<Void> {
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
    companion object {
        @JvmStatic
        fun newInstance(token: String, estudiante: Int, curso: Int) =
            IngresarCalificacionesFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TOKEN, token)
                    putSerializable(ESTUDIANTE, estudiante)
                    putSerializable(CURSO, curso)
                }
            }
    }

    private fun showToast(message: String) {
        // Muestra un Toast con el mensaje proporcionado
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
}
