package com.javierprado.jmapp.view.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.databinding.FragmentJustificarFaltaBinding
import com.javierprado.jmapp.databinding.FragmentNotificacionesUsuarioBinding
import com.javierprado.jmapp.view.activities.control.ControlEstudianteActivity
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import com.javierprado.jmapp.view.activities.menus.MenuApoderadoActivity
import java.io.Serializable


class JustificarFaltaFragment : Fragment() {
    lateinit var binding: FragmentJustificarFaltaBinding
    private lateinit var activity: AppCompatActivity
    var apoderadoId = "" ; var token = "" ; var hijos: List<Estudiante> = ArrayList()

    private val PICK_FILE_REQUEST_CODE = 123 ; private val PICK_IMG_REQUEST_CODE = 321
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as ControlEstudianteActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            apoderadoId = it.getString(MenuAdministradorActivity().TOKEN, "")
            token = it.getString(MenuAdministradorActivity().USUARIOID, "")
            hijos = it.getSerializable(MenuApoderadoActivity().HIJOS) as List<Estudiante>
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentJustificarFaltaBinding.inflate(inflater, container, false)
        return binding.root
    }
    companion object {
        fun newInstance(token: String, apoderadoId: String, estudiantes: Serializable) =
            JustificarFaltaFragment().apply {
                arguments = Bundle().apply {
                    putString(MenuAdministradorActivity().TOKEN, token)
                    putString(MenuAdministradorActivity().USUARIOID, apoderadoId)
                    putSerializable(MenuApoderadoActivity().HIJOS, estudiantes)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSubirImg.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, PICK_IMG_REQUEST_CODE)
        }
        binding.btnSubirDoc.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
        }

    }
}