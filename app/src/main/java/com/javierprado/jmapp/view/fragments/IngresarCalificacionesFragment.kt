package com.javierprado.jmapp.view.fragments

import Calificacion
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.fragment.app.DialogFragment
import androidx.room.util.getColumnIndex
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
    private lateinit var calificacion: Calificacion
    private var cursoId: Int = 0
    private lateinit var msg : String
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

        txtPrimeraNota.addTextChangedListener(textWatcher)
        txtSegundaNota.addTextChangedListener(textWatcher)
        txtTerceraNota.addTextChangedListener(textWatcher)
        txtCuartaNota.addTextChangedListener(textWatcher)

        return view
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            fun validarNum(num: Int?): Boolean{
                return (num==null) or (num!!>20)
            }
            fun calcularPromedio(): Double{
                try{
                    val n1 = txtPrimeraNota.text.toString().toIntOrNull() ; val n2 = txtSegundaNota.text.toString().toIntOrNull()
                    val n3 = txtTerceraNota.text.toString().toIntOrNull() ; val n4 = txtCuartaNota.text.toString().toIntOrNull()

                    return (n1!!+n2!!+n3!!+n4!!) / 4.0
                }
                catch(ex: NullPointerException){
                    return 0.0
                }
            }

            txtNotaFinal.setText("")
            if(!s.isNullOrEmpty() && !validarNum(s.toString().toIntOrNull())){
                val txts: List<EditText> = listOf(txtPrimeraNota, txtSegundaNota, txtTerceraNota, txtCuartaNota)
                val values_txts: List<String> = txts.map { it.text.toString() }
                txts.mapIndexed { index, editText -> if(values_txts[index].isEmpty()) txts[index].setText("0") }
                val promedio = calcularPromedio()
                txtNotaFinal.setText(promedio.toString())
            }
            btnGuardarCalificaciones.isEnabled = txtNotaFinal.text.toString().isNotEmpty()
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val api = retro.getApi()

        api.obtenerCalificaciones(estudianteId, cursoId).enqueue(object : Callback<Calificacion> {
            override fun onResponse(call: Call<Calificacion>, response: Response<Calificacion>) {
                if (response.isSuccessful) {
                    val calificaciones = response.body()
                    calificacion = calificaciones!!
                    val nota1 = calificacion.calificacion1.toString()
                    val nota2 = calificacion.calificacion2.toString()
                    val nota3 = calificacion.calificacion3.toString()
                    val nota4 = calificacion.calificacion4.toString()
                    val notaF = calificacion.calificacionFinal.toString()

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

            calificacion.calificacion1 = primeraNota
            calificacion.calificacion2 = segundaNota
            calificacion.calificacion3 = terceraNota
            calificacion.calificacion4 = cuartaNota
            calificacion.calificacionFinal = notaFinal

            // Realiza la llamada al método de la API para ingresar las calificaciones
            api.editarCalificacion(calificacion).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    msg = response.headers()["message"] ?: ""
                    if (response.isSuccessful) { activity.supportFragmentManager.popBackStackImmediate() }
                    else { showToast(msg) }
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
