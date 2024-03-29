package com.javierprado.jmapp.view.activities.menus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.javierprado.jmapp.R
import com.javierprado.jmapp.model.NewsAdapter
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.login.OptionLogin
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.util.ExtraFunctions
import com.javierprado.jmapp.data.util.NavigationWindows
import com.javierprado.jmapp.view.activities.control.ControlSeleccionActivity
import com.javierprado.jmapp.view.fragments.SeleccionarAulaFragment

class MenuDocenteActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toogle: ActionBarDrawerToggle

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnMasRecientes: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var api : ColegioAPI
    val TOKEN = "token"
    var tokenDoc = ""
    private var extraFuns : ExtraFunctions = ExtraFunctions()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_docente)

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

        auth = FirebaseAuth.getInstance()

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
        if (!isNull){
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
    fun actualizarNoticias(token: String){
        val adapter = NewsAdapter(this@MenuDocenteActivity, ArrayList(), api, token, true)
        extraFuns.listarNoticias(api, adapter, this@MenuDocenteActivity)
        recyclerView.adapter = adapter
    }
}