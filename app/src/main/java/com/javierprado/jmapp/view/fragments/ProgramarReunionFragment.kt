package com.javierprado.jmapp.view.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Docente
import com.javierprado.jmapp.data.entities.Reunion
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.databinding.FragmentProgramarReunionBinding
import com.javierprado.jmapp.mvvm.ChatAppViewModel
import com.javierprado.jmapp.view.activities.control.ControlEstudianteActivity
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import com.javierprado.jmapp.view.activities.menus.MenuAdministradorActivity
import com.javierprado.jmapp.view.adapters.DocenteAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

class ProgramarReunionFragment : Fragment() {
    lateinit var binding: FragmentProgramarReunionBinding
    val REUNION = "reunion" ; val IDREUNION = "reunionid"
    var docentes: List<Docente> = ArrayList()
    private lateinit var reunion: Reunion
    var aulaId = "" ; var usuarioId = ""; var estudianteId = "" ; lateinit var docente: Docente
    var apoderadoId = ""
    var infoApoderado = "" ; var ocupadas: MutableList<String> = ArrayList()
    private val TAG: String = toString()
    private val retro = RetrofitHelper.getInstanceStatic()
    private var toProgramar = false
    private lateinit var activity: AppCompatActivity
    private lateinit var msg : String
    private lateinit var api: ColegioAPI
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            activity = context as ControlSeleccionActivity
        }catch (e: Exception){
            activity = context as ControlEstudianteActivity
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            retro.setBearerToken(it.getString(MenuAdministradorActivity().TOKEN, ""))
            api = retro.getApi()
            reunion = try{ it.getSerializable(REUNION) as Reunion } catch(c: ClassCastException){ Reunion() }
            toProgramar = reunion.id == ""
            aulaId = it.getString(ControlEstudianteActivity().AULAID, "").split("-")[0]
            usuarioId = it.getString(MenuAdministradorActivity().USUARIOID, "")
            estudianteId = reunion.estudianteId ; apoderadoId = reunion.apoderadoId
            if(toProgramar){
                estudianteId = it.getString(ControlEstudianteActivity().AULAID, "").split("-")[1]
            }
            //VARIABLES
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProgramarReunionBinding.inflate(inflater, container, false)
        return binding.root
    }
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //MODO APODERADO
        if(toProgramar){
            binding.fgBtn1Reunion.visibility = View.GONE ; binding.txtApoderadoReunion.visibility = View.GONE
            binding.fgBtn2Reunion.text = "Programar"

            val adapter = DocenteAdapter(activity, ArrayList())
            binding.sDocenteReunion.adapter = adapter
            getDocentes(adapter)

            binding.sDocenteReunion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    docente = adapter.getItem(position)
                    try{ validacion() }
                    catch(d: DateTimeParseException ){ msg = "El formato de la fecha no es el correcto." }
                    catch (e: Exception){ Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show() }
                }
            }
        }else{
            binding.sDocenteReunion.visibility = View.GONE ; binding.txtEstadoReunion.visibility = View.GONE ;
            binding.estadoReunion.visibility = View.GONE ;
            binding.fgBtn2Reunion.isEnabled=true ; binding.fgBtn1Reunion.isEnabled=true
            binding.txtEnviarReunion.text = "Apoderado:"; binding.fgBtn2Reunion.text = "Reprogramar"
            val nsa = reunion.apoderadoId.split("-")[0] ; var nse = reunion.estudianteId.split("-")[0]
            var infoe = reunion.estudianteId.split("-")[1]
            infoApoderado = nsa + "\n" + infoe + "\n" + nse
            binding.txtApoderadoReunion.text = infoApoderado
            binding.fechaReunion.setText(reunion.fecha) ; binding.horaReunion.setText(reunion.horaInicio)
        }

        binding.fgBtn1Reunion.setOnClickListener { funcionBtn1() }
        binding.fgBtn2Reunion.setOnClickListener { funcionBtn2() }

        binding.fechaReunion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                msg = "" ; try{ validacion() } catch (e: Exception){ msg = e.message ?: "" }
                if(msg.isNotEmpty()){ Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show() }
            }
        })
        binding.horaReunion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                msg = "" ; try{ validacion() } catch (e: Exception){ msg = e.message ?:"" }
                if(msg.isNotEmpty()){ Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show() }
            }
        })

    }

    private fun funcionBtn1() {
        reunion.estado = "PROGRAMADA"
        val id = reunion.id
        api.guardarReunion(reunion, id).enqueue(object : Callback<Reunion> {
            override fun onResponse(call: Call<Reunion>, response: Response<Reunion>) {
                msg = response.headers()["message"] ?: ""
                if (response.isSuccessful) {
                    //ENVIAR CORREO A DOCENTE
                    val reuActualizada = response.body()!!
                    ChatAppViewModel().accionReuniones(AnotherUtil.getUidLoggedIn(),
                        reunion.apoderadoId.split("-")[1], "D_ACEPTA", reuActualizada)
                    Toast.makeText(activity, "Se envió la notificación de aprobación.", Toast.LENGTH_SHORT).show()
                    activity.supportFragmentManager.popBackStackImmediate()
                }else{
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                    Log.e("REUNION", msg)
                }
            }
            override fun onFailure(call: Call<Reunion>, t: Throwable) {
                Toast.makeText(activity, "FAIL", Toast.LENGTH_SHORT).show()
                Log.e("REUNION", t.message.toString())
            }
        } )
    }

    private fun funcionBtn2() {
        val fecha = binding.fechaReunion.text.toString() ; val hora = binding.horaReunion.text.toString()
        val estado = "RESPUESTA_A"

        val reunionGuardar = Reunion(fecha, hora,
        if(!toProgramar) usuarioId else docente.id, if(toProgramar) usuarioId else apoderadoId, estudianteId)
        var id = reunion.id

        //sender: String, emailReceiver: String, accion: String, reunion: Reunion
        //IDUSUARIOACTUAL - EMAIL DOC o APOD, PROGRAMAR, REUNIONCREADA - EDITADA, SI EL RECEIVER ES EMAIL O USER

        if(toProgramar){
            id = "null"
        }else{
            val txtButton = "Enviar"
            if(binding.fgBtn2Reunion.text.toString() != txtButton){
                ocupadas.add(fecha+hora)
                binding.fechaReunion.text.clear() ; binding.horaReunion.text.clear()
                binding.fgBtn1Reunion.visibility=View.GONE ; binding.fgBtn2Reunion.text=txtButton
                binding.fechaReunion.isEnabled=true ; binding.horaReunion.isEnabled=true
                binding.fechaReunion.requestFocus()
                //ABRIR TECLADO
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.fechaReunion, InputMethodManager.SHOW_IMPLICIT) ; return
            }
            reunionGuardar.estado = estado
        }
        api.guardarReunion(reunionGuardar, id).enqueue(object : Callback<Reunion> {
            override fun onResponse(call: Call<Reunion>, response: Response<Reunion>) {
                msg = response.headers()["message"] ?: ""
                if (response.isSuccessful) {
                    val reuCreada = response.body()!!
                    val cidApoderado = reuCreada.apoderadoId ; val cidDocente = reuCreada.docenteId
                    val accion = if(toProgramar){ "A_PROGRAMA" } else { "D_REPROGRAMA" }
                    val correo = if(toProgramar) { cidDocente.split("-")[0] } else{ cidApoderado.split("-")[0] }

                    reuCreada.apoderadoId = cidApoderado.split("-")[1]
                    reuCreada.docenteId = cidDocente.split("-")[1]
                    listOf(AnotherUtil.getUidLoggedIn(), correo, accion, reuCreada).map { xd->Log.e("XD",xd.toString()) }
                    ChatAppViewModel().accionReuniones(AnotherUtil.getUidLoggedIn(), correo, accion, reuCreada)
                    Toast.makeText(activity,
                        "Se ha enviado la notificacion para solicitar"+if(toProgramar) "programación de reunión." else " la reprogramación.", Toast.LENGTH_SHORT).show()
                    activity.supportFragmentManager.popBackStackImmediate()
                }else{
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                    Log.e("REUNION", msg)
                }
            }
            override fun onFailure(call: Call<Reunion>, t: Throwable) {
                Toast.makeText(activity, "FAIL", Toast.LENGTH_SHORT).show()
                Log.e("REUNION", t.message.toString())
            }
        } )
    }

    companion object {
        @JvmStatic
        fun newInstance(token: String, reunion: Serializable, usuarioId: String, aulaId: String) =
            ProgramarReunionFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(MenuAdministradorActivity().TOKEN, token)
                    putSerializable(REUNION, reunion)
                    putSerializable(MenuAdministradorActivity().USUARIOID, usuarioId)
                    putSerializable(ControlEstudianteActivity().AULAID, aulaId)
                }
            }
    }

    @SuppressLint("SetTextI18n")
    fun validacion(){
        binding.estadoReunion.setText("No disponible")
        var error = ""
        if(toProgramar){
            ocupadas = docente.reuniones as MutableList<String>
        }
        val fecha = binding.fechaReunion.text.toString()
        var validacion = fecha.length==10
        if(validacion){
            var fechaLD: LocalDate
            try{
                fechaLD = LocalDate.parse(fecha)
                if(fechaLD.isBefore(LocalDate.now())) { error = "La fecha debe ser posterior a la fecha actual." }
                if(arrayOf(DayOfWeek.SUNDAY, DayOfWeek.SATURDAY).contains(fechaLD.dayOfWeek)) { error = "Programar reunión un día de L-V." }
            }catch(d: DateTimeParseException ){ error = "El formato de la fecha no es el correcto." }
            if(error.isNotEmpty()){ binding.fechaReunion.text.clear(); throw Exception(error) }
        }
        val hora = binding.horaReunion.text.toString() ; validacion = hora.length==5
        if(validacion){
            try{
                LocalTime.parse(hora) ; val regex = Regex("^(1[5-7]):[0-5][0-9]\$")
                if(!regex.matches(hora)){ error = "La hora de reuniones son de 3PM a 6PM." }
                else{
                    val minutos = hora.split(":")[1].toInt()
                    if(minutos % 5 != 0) {
                        binding.horaReunion.setText(hora.split(":")[0]+":")
                        throw Exception("El tiempo debe ser de 15 minutos.")
                    }
                }
            } catch(d: DateTimeParseException ){ error = "El formato de la hora no es correcto." }
            if(error.isNotEmpty()){ binding.horaReunion.text.clear(); throw Exception(error) }
        }
        if(ocupadas.contains(fecha+hora)){
            error = if(toProgramar) "Ya hay una reunion para esta hora y fecha." else "Ya tienes una reunión para esta hora y fecha."
            if(!toProgramar && ocupadas[ocupadas.lastIndex] == fecha+hora){
                error = "Esta fecha y hora son las mismas que las sugeridas por el apoderado."
            }
            binding.horaReunion.text.clear(); binding.fechaReunion.text.clear()
            if(error.isNotEmpty()){ binding.horaReunion.text.clear(); throw Exception(error) }
        }
        binding.estadoReunion.setText(if (validacion) "Disponible" else "No disponible")

        binding.estadoReunion.setBackgroundColor(if (validacion) Color.parseColor("#3E8914")
        else Color.parseColor("#BA3B46"))
        binding.fgBtn2Reunion.isEnabled = validacion
    }

    fun getDocentes(adapter: DocenteAdapter){
        api.listarDocentes("null","null", aulaId).enqueue(object : Callback<List<Docente>> {
            override fun onResponse(call: Call<List<Docente>>, response: Response<List<Docente>>) {
                msg = response.headers()["message"] ?: ""
                if (response.isSuccessful) {
                    docentes = response.body()!!
                    binding.sDocenteReunion.adapter = adapter
                    binding.fechaReunion.isEnabled=true ; binding.horaReunion.isEnabled = true
                    adapter.setList(docentes)
                }else{
                    Log.e("DOCENTES:", msg)
                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                    activity.supportFragmentManager.popBackStackImmediate()
                }
            }
            override fun onFailure(call: Call<List<Docente>>, t: Throwable) {
                Log.e("DOCENTES", t.message.toString())
            }
        } )
    }
// override fun onDestroy() {
// val intent = Intent(context, AnteriorActivity::class.java)
// intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
//    Intent.FLAG_ACTIVITY_SINGLE_TOP
// startActivity(intent)
// super.onDestroy()
// }
}
