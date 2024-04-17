package com.javierprado.jmapp.view.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.javierprado.jmapp.R

class RegistroDeNotificacionFragment : Fragment() {

    private lateinit var botonEnviar: Button
    private lateinit var tituloEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var autoCompleteTextView: AutoCompleteTextView

    private var destinatarioSeleccionado: String = ""
    private var titulo: String = ""
    private var descripcion: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_registro_de_noticiaciones, container, false)

        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView)
        botonEnviar = view.findViewById(R.id.fg_btn_funcion)
        tituloEditText = view.findViewById(R.id.txt_NotiTitulo)
        descripcionEditText = view.findViewById(R.id.txt_NotiDescrip)

        // Agregar TextWatchers a los EditText
        tituloEditText.addTextChangedListener(textWatcher)
        descripcionEditText.addTextChangedListener(textWatcher)

        // Deshabilitar el botón al inicio
        botonEnviar.isEnabled = false

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar el adaptador para el AutoCompleteTextView
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line)
        autoCompleteTextView.setAdapter(adapter)

        // Manejar el evento de cambio de texto en el AutoCompleteTextView
        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val nombreCompleto = s.toString()
                if (nombreCompleto.isNotBlank()) {
                    val nombres = nombreCompleto.split(" ")
                    if (nombres.size >= 2) {
                        val nombre = nombres[0]
                        val apellido = nombres[1]
                        buscarUsuarios(nombre, apellido)
                    }
                } else {
                    // Limpiar la lista de sugerencias si el campo está vacío
                    adapter.clear()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Agregar un listener para manejar la selección de usuario en el AutoCompleteTextView
        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            destinatarioSeleccionado = parent.getItemAtPosition(position) as String
        }

        // lógica del botón
        botonEnviar.setOnClickListener {
            titulo = tituloEditText.text.toString()
            descripcion = descripcionEditText.text.toString()

            // Verificar si los campos están completos
            if (titulo.isNotEmpty() && descripcion.isNotEmpty() && destinatarioSeleccionado.isNotEmpty()) {
                // Enviar Noti
                enviarNotificacion(destinatarioSeleccionado, titulo, descripcion)
            } else {
                // Mostrar mensaje de error si falta algún campo
                Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buscarUsuarios(nombre: String, apellido: String) {
        val db = FirebaseFirestore.getInstance()
        val usuariosRef = db.collection("usuarios")

        // Realizar la consulta en Firestore
        usuariosRef.whereEqualTo("nombres", nombre)
            .whereEqualTo("apellidos", apellido)
            .get()
            .addOnSuccessListener { result ->
                val usuarios = mutableListOf<String>()
                for (document in result) {
                    val nombreCompleto = "${document.getString("nombres")} ${document.getString("apellidos")}"
                    usuarios.add(nombreCompleto)
                }
                // Actualizar el adaptador con los resultados de la búsqueda
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, usuarios)
                autoCompleteTextView.setAdapter(adapter)
            }
            .addOnFailureListener { exception ->
                // Manejar errores
            }
    }

    private fun enviarNotificacion(destinatario: String, titulo: String, descripcion: String) {
        // Aquí iría la lógica para enviar la notificación
        // Mostrar mensaje de envío correcto
        Toast.makeText(requireContext(), "Notificación enviada correctamente", Toast.LENGTH_SHORT).show()

        // Regresar a la actividad anterior
        requireActivity().finish()
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            // Verificar si todos los campos están llenos para habilitar el botón
            val titulo = tituloEditText.text.toString().trim()
            val descripcion = descripcionEditText.text.toString().trim()
            botonEnviar.isEnabled =
                titulo.isNotEmpty() && descripcion.isNotEmpty() && destinatarioSeleccionado.isNotEmpty()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }
}
