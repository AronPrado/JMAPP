package com.javierprado.jmapp.view.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.data.entities.Justificacion
import com.javierprado.jmapp.data.entities.Reunion
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.databinding.FragmentJustificarFaltaBinding
import com.javierprado.jmapp.databinding.FragmentNotificacionesUsuarioBinding
import com.javierprado.jmapp.mvvm.ChatAppViewModel
import com.javierprado.jmapp.view.activities.control.ControlEstudianteActivity
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import com.javierprado.jmapp.view.activities.menus.MenuApoderadoActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable


class JustificarFaltaFragment : Fragment() {
    val JUSTIFICACION = "justificaciones"
    lateinit var binding: FragmentJustificarFaltaBinding
    private var apoderadoId = "" ; private var hijos: List<Estudiante> = ArrayList()
    private val storageRef = FirebaseStorage.getInstance().reference
    private val retro = RetrofitHelper.getInstanceStatic()
    private lateinit var api: ColegioAPI

    private var asistenciaId = ""
    private var imgURI: Uri? = null; private var docURI: Uri? = null

    private val PICK_FILE_REQUEST_CODE = 123 ; private val PICK_IMG_REQUEST_CODE = 321
    private lateinit var activity: AppCompatActivity
    private lateinit var msg : String
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as ControlEstudianteActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            apoderadoId = it.getString(MenuAdministradorActivity().TOKEN, "")
            hijos = it.getSerializable(MenuApoderadoActivity().HIJOS) as List<Estudiante>
            retro.setBearerToken(it.getString(MenuAdministradorActivity().USUARIOID, ""))
            api = retro.getApi()
            asistenciaId = it.getString("ASISTENCIA","")
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
        fun newInstance(token: String, apoderadoId: String, asistenciaId: String) =
            JustificarFaltaFragment().apply {
                arguments = Bundle().apply {
                    putString(MenuAdministradorActivity().TOKEN, token)
                    putString(MenuAdministradorActivity().USUARIOID, apoderadoId)
                    putSerializable("ASISTENCIA", asistenciaId)
                }
            }
    }
    val launcherImg = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imgURI = uri ; Log.e("RESULT_OK", "SE OBTUVO CORRECTAMENTE LA IMAGEN")
            binding.btnJustificar.isEnabled=true
        }
    }
    val launcherDoc = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        binding.btnJustificar.isEnabled=false
        if (uri != null) {
            val mimeType = activity.contentResolver.getType(uri)
            if (mimeType.equals("application/pdf") || mimeType.equals("application/msword")) {
                docURI = uri
                Log.e("RESULT_OK", "SE OBTUVO CORRECTAMENTE EL PDF")
                binding.btnJustificar.isEnabled=true
            } else {
                Toast.makeText(activity, "Solo se permiten archivos PDF o Word", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSubirImg.setOnClickListener {
            launcherImg.launch("image/*") // Only images
        }

        binding.btnSubirDoc.setOnClickListener {
            launcherDoc.launch("application/pdf|application/msword") // PDF or Word only
        }
//        binding.btnSubirImg.setOnClickListener{
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "image/*"
//            startActivityForResult(intent, PICK_IMG_REQUEST_CODE)
//        }
//        binding.btnSubirDoc.setOnClickListener{
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "application/pdf|application/msword"
//            startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
//        }
        binding.btnJustificar.setOnClickListener {
            val archivos = listOf(imgURI, docURI)
            if(imgURI == null && docURI == null){
                Log.e("NO","NO SE SELECCIONARON ARCHIVOS")
            }else{
                Log.e("archivos", archivos[0].toString() + "---"+ archivos[1].toString())
                Log.e("PRUEBANULL", (archivos[0] == null).toString()+"---"+(archivos[1] == null).toString())
                val justificacion = Justificacion(asistenciaId, apoderadoId, "")
                api.guardarJustificacion(justificacion, "null").enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        msg = response.headers()["message"] ?: ""
                        if (response.isSuccessful) {
                            val id = msg
                            guardarEnStorge(imgURI, true, id) ; guardarEnStorge(docURI, false, id)
                            activity.supportFragmentManager.popBackStackImmediate()
                            var accion = "" ; justificacion.id=id
                            ChatAppViewModel().accionJustificaciones("ln75111300@idat.edu.pe", accion, justificacion, false)
                        }else{
                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                            Log.e("JUSTIFICACION:", msg)
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(activity, "FAIL", Toast.LENGTH_SHORT).show()
                        Log.e("JUSTIFICACION", t.message.toString())
                    }
                } )
            }
        }
    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            if(requestCode == PICK_IMG_REQUEST_CODE){
//                imgURI = data?.data ?: return
//                Log.e("RESULTOK","SE OBTUVO CORRECTAMENTE LA IMAGEN")
//            }else if(requestCode == PICK_FILE_REQUEST_CODE){
//                docURI = data?.data ?: return
//                Log.e("RESULTOK","SE OBTUVO CORRECTAMENTE EL PDF")
//            }
//            binding.btnJustificar.isEnabled = true
//        }
//    }
    fun guardarEnStorge(archivo: Uri?, img: Boolean, id: String){
        if(archivo != null){
            val carpeta = if (img) "imagenes" else "documentos"
            // Crear una referencia de almacenamiento para el archivo
            val fileRef = storageRef.child("$carpeta/$id")
            // Subir el archivo
            fileRef.putFile(archivo).addOnSuccessListener {
                // Archivo subido correctamente
                Log.d("STORAGE", "Archivo subido: ${it.metadata?.path}")
            }.addOnFailureListener {
                // Error al subir el archivo
                Log.e("STORAGE", "Error al subir archivo: ${it.message}")
            }
        }
    }
}