package com.javierprado.jmapp.view.activities.menus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Usuario
import com.javierprado.jmapp.model.NewsAdapter
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.login.OptionLogin
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.data.util.ExtraFunctions
import com.javierprado.jmapp.data.util.NavigationWindows
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.view.activities.comunicacion.ChatDocenteApoderadoActivity
import com.javierprado.jmapp.view.activities.control.ControlHorarioActivity
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import com.javierprado.jmapp.view.fragments.SeleccionarAulaFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class MenuDocenteActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object{
        lateinit var instance : MenuDocenteActivity
    }
    private lateinit var drawer: DrawerLayout
    private lateinit var toogle: ActionBarDrawerToggle

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnMasRecientes: ImageView

    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    private lateinit var api : ColegioAPI
    val TOKEN = "token"
    val ID = "docenteId"
    var tokenDoc = ""
    var docenteId = 0
    private lateinit var msg : String
    private var extraFuns : ExtraFunctions = ExtraFunctions()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_docente)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        btnMasRecientes = findViewById(R.id.btn_mas_reciente)
        //API Y BUNDLE
        val retro = RetrofitHelper.getInstanceStatic()
        val bundle = intent.extras
        if (bundle != null) {
            val token = bundle.getString(TOKEN, "")
            tokenDoc=token
            retro.setBearerToken(token)
        }
        api = retro.getApi()
        //BORRAR
//        val user = auth.currentUser
//        if(user!=null){
//            val dataHashMap = hashMapOf("userid" to user.uid, "info" to "Franco Prueba Martinez Ryley", "correo" to "martinfr@hotmail.com", "estado" to "default", "tipo" to "DOC", "tipoid" to "31", "token" to "")
//            firestore.collection("Users").document(user.uid).set(dataHashMap).addOnCompleteListener {
//                    task -> Log.e("ERROR FSTORE", task.exception.toString()) }
//        }
        //BORRAR
        //Noticias
        recyclerView = findViewById(R.id.recyclerViewNews)
        recyclerView.layoutManager = LinearLayoutManager(this)

        actualizarNoticias(retro.getBearerToken())
        btnMasRecientes.setOnClickListener { actualizarNoticias(retro.getBearerToken()) }

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)

        toogle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawer.addDrawerListener(toogle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view_docente)
        navigationView.setNavigationItemSelectedListener(this)

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var clase: AppCompatActivity? = null
        val intent: Intent
        var transport = NavigationWindows.FUNCIONES.name
        when(item.itemId){
            R.id.nav_item_1 -> Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
            R.id.nav_item_2 -> {
                Toast.makeText(this, "Seleccionar Seccion", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_item_3 -> {
                Toast.makeText(this, "Ingresar notas", Toast.LENGTH_SHORT).show()
                transport = NavigationWindows.NOTAS.name
            }
            R.id.nav_item_4 -> {
                Toast.makeText(this, "Asistencia Escolar", Toast.LENGTH_SHORT).show()
                transport = NavigationWindows.ASISTENCIAS.name
            }
            R.id.nav_item_5 -> {
                Toast.makeText(this, "Programar Tareas y Evaluaciones", Toast.LENGTH_SHORT).show()
                transport = NavigationWindows.EVALUACIONES.name
            }
            R.id.nav_item_6 -> {
                Toast.makeText(this, "Notificar Tareas", Toast.LENGTH_SHORT).show()
                transport = NavigationWindows.TAREAS.name
            }
            R.id.nav_item_7 -> {
                Toast.makeText(this, "Comunicarse con el apoderado", Toast.LENGTH_SHORT).show()
                clase =  ChatDocenteApoderadoActivity()
                firestore.collection("Users").document(AnotherUtil.getUidLoggedIn()).update("token", tokenDoc)
                transport = NavigationWindows.COMUNICACION.name
            }
            R.id.nav_item_8 -> {
                auth.signOut()
                clase = OptionLogin()
            }
        }
        val isNull = clase == null
        clase = if (isNull) ControlSeleccionActivity() else clase
        intent = Intent(this, clase!!::class.java)
        if (!isNull && transport != NavigationWindows.COMUNICACION.name){
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }else{
            intent.putExtra(ControlSeleccionActivity().TOKEN, tokenDoc)
            intent.putExtra(SeleccionarAulaFragment().DIRECT, transport)
        }
        startActivity(intent)

        Log.e("TRANSPORTE",transport)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    //Iconos del Toolbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.perfil_noti_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_perfil -> {
                // Maneja la acción de Configuración
                true
            }
            R.id.action_notificaciones -> {
                // Maneja la acción de Búsqueda
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun actualizarNoticias(token: String) {
        val adapter = NewsAdapter(this@MenuDocenteActivity, ArrayList(), api, token, true)
        extraFuns.listarNoticias(api, adapter, this@MenuDocenteActivity)
        recyclerView.adapter = adapter
    }
}