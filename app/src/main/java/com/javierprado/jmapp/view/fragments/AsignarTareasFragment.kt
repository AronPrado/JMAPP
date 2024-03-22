package com.javierprado.jmapp.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity

class AsignarTareasFragment : Fragment() {

    private lateinit var btnAsignar: Button
    private lateinit var descripcionTarea: EditText
    private lateinit var fechaEntrega: EditText

    private lateinit var progressC: CircularProgressIndicator

    private lateinit var msg : String

    val TOKEN = "token"
    private val TAG: String  = toString()
    private val retro = RetrofitHelper.getInstanceStatic()
    private lateinit var activity: AppCompatActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as ControlSeleccionActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            retro.setBearerToken(it.getString(TOKEN, ""))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_asignar_tareas, container, false)
        btnAsignar = view.findViewById(R.id.btn_asignar_tarea)
        descripcionTarea = view.findViewById(R.id.txt_descripcion_tarea)
        fechaEntrega = view.findViewById(R.id.txt_fechaentrega_tarea)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val api = retro.getApi()
    }
    companion object {
        @JvmStatic
        fun newInstance(token: String) =
            SeleccionarAulaFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TOKEN, token)
                }
            }
    }
}