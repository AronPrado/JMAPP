package com.javierprado.jmapp.view.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.activities.menus.MenuDocenteActivity
class ConfirmarDisponibilidadFragment : Fragment() {
    private lateinit var cambiarspinnerSpinner: Spinner
    private lateinit var cambiarbuttonBtn: Button
    private lateinit var cambiartxtTxt: TextView
    private lateinit var cambiareditEtxt: EditText
    private lateinit var progressC: CircularProgressIndicator
    //VARIABLES
    private lateinit var msg : String
    val TOKEN = "token"
    private val TAG: String = toString()
    private val retro = RetrofitHelper.getInstanceStatic()
    private lateinit var activity: AppCompatActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MenuDocenteActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            retro.setBearerToken(it.getString(TOKEN, ""))
            //VARIABLES
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_confirmar_disponibilidad, container,false)
//        cambiarspinnerSpinner = view.findViewById(R.id.s_nivel_educativo_horario)
//        cambiarbuttonBtn = view.findViewById(R.id.btn_next_horario)
//        cambiartxtTxt = view.findViewById(R.id.txt_mes_horario)
//        cambiareditEtxt = view.findViewById(R.id.edit_txt)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val api = retro.getApi()
        // FUNCIONES
        // APIS
        // BOTONES
    }
    companion object {
        @JvmStatic
        fun newInstance(token: String) =
            ConfirmarDisponibilidadFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(TOKEN, token)
//VARIABLES
                }
            }
    }
// override fun onDestroy() {
// val intent = Intent(context, AnteriorActivity::class.java)
// intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
//    Intent.FLAG_ACTIVITY_SINGLE_TOP
// startActivity(intent)
// super.onDestroy()
// }
}
