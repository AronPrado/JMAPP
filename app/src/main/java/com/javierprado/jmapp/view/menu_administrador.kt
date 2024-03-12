package com.javierprado.jmapp.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.javierprado.jmapp.clases.NewsAdapter
import com.javierprado.jmapp.data.entities.Noticia
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.ExtraFunctions
import com.javierprado.jmapp.view.agregar.ControlNoticiaActivity
import com.javierprado.jmapp.view.agregar.RegisterDocenteActivity
import com.javierprado.jmapp.view.login.OptionLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class menu_administrador : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toogle: ActionBarDrawerToggle

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnMasRecientes: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var api : ColegioAPI

    val TOKEN = "token"
    var tokenAdmin = ""

    private var extraFuns : ExtraFunctions = ExtraFunctions()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_administrador)

        btnMasRecientes = findViewById(R.id.btn_mas_reciente)
        //API Y BUNDLE
        val retro = RetrofitHelper.getInstanceStatic()
        val bundle = intent.extras
        if (bundle != null) {
            val token = bundle.getString(TOKEN, "")
            tokenAdmin=token
            retro.setBearerToken(tokenAdmin)
        }
        api = retro.getApi()
        //Noticias
        recyclerView = findViewById(R.id.recyclerViewNews)
        recyclerView.layoutManager = LinearLayoutManager(this)

        actualizarNoticias(retro.getBearerToken())
        btnMasRecientes.setOnClickListener { actualizarNoticias(retro.getBearerToken()) }

        auth = FirebaseAuth.getInstance()

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)

        toogle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawer.addDrawerListener(toogle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view_administrador)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_item_1 -> Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
            R.id.nav_item_2 -> {
                val intent = Intent(this, RegisterDocenteActivity::class.java)
                intent.putExtra(RegisterDocenteActivity().TOKEN, tokenAdmin)
                startActivity(intent)
            }
            R.id.nav_item_3 -> {
                Toast.makeText(this, "Redactar y Enviar Notificaciones", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, NotiEventosEsco::class.java)
                startActivity(intent)
            }
            R.id.nav_item_4 -> Toast.makeText(this, "Editar Horario Escolar", Toast.LENGTH_SHORT).show()
            R.id.nav_item_5 -> {
                val intent = Intent(this, ControlNoticiaActivity::class.java)
                intent.putExtra(ControlNoticiaActivity().TOKEN, tokenAdmin)
                startActivity(intent)
                Toast.makeText(this, "Agregar Noticia", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_item_6 -> Toast.makeText(this, "Docentes Registrados", Toast.LENGTH_SHORT).show()
            R.id.nav_item_8 ->  {
                auth.signOut()
                val intent = Intent(this, OptionLogin::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }

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

                true
            }
            R.id.action_notificaciones -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun actualizarNoticias(token: String){
        val adapter = NewsAdapter(this@menu_administrador, ArrayList(), api, token, false)
        recyclerView.adapter = adapter
        extraFuns.listarNoticias(api, adapter, this@menu_administrador)
    }
}