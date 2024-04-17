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
import com.javierprado.jmapp.view.adapters.NoticiaAdapter
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.ExtraFunctions
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.view.NotiEventosEsco
import com.javierprado.jmapp.view.activities.agregar.RegisterApoderadoActivity
import com.javierprado.jmapp.view.activities.agregar.RegisterDocenteActivity
import com.javierprado.jmapp.view.activities.control.ControlHorarioActivity
import com.javierprado.jmapp.view.activities.control.ControlNoticiaActivity
import com.javierprado.jmapp.view.activities.notificaciones.RegistroDeNotificacionActivity
import com.javierprado.jmapp.view.fragments.RegistroDeNotificacionFragment
import com.javierprado.jmapp.view.login.OptionLogin

class MenuAdministradorActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toogle: ActionBarDrawerToggle

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnMasRecientes: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var api : ColegioAPI

    val TOKEN = "token"; val USUARIOID = "usuarioid"
    var tokenAdmin = ""; var adminId = ""

    private var extraFuns : ExtraFunctions = ExtraFunctions()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_administrador)

        btnMasRecientes = findViewById(R.id.btn_mas_reciente)
        //API Y BUNDLE
        val retro = RetrofitHelper.getInstanceStatic()
        val bundle = intent.extras
        if (bundle != null) {
            tokenAdmin=bundle.getString(TOKEN, "")
            adminId=bundle.getString(USUARIOID, "")
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
                val intent = Intent(this, RegisterApoderadoActivity::class.java)
                intent.putExtra(USUARIOID, adminId)
                intent.putExtra(RegisterApoderadoActivity().TOKEN, tokenAdmin)
                startActivity(intent)
            }
            R.id.nav_item_3 -> {
                val intent = Intent(this, RegisterDocenteActivity::class.java)
                intent.putExtra(USUARIOID, adminId)
                intent.putExtra(RegisterDocenteActivity().TOKEN, tokenAdmin)
                startActivity(intent)
            }
            R.id.nav_item_4 -> {
                Toast.makeText(this, "Redactar y Enviar Notificaciones", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, RegistroDeNotificacionActivity::class.java)
                intent.putExtra(USUARIOID, adminId)
                startActivity(intent)
            }
            R.id.nav_item_5 -> {
//                Toast.makeText(this, "Editar Horario Escolar", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ControlHorarioActivity::class.java)
                intent.putExtra(MenuAdministradorActivity().USUARIOID, adminId)
                intent.putExtra(MenuAdministradorActivity().TOKEN, tokenAdmin)
                intent.putExtra(ControlHorarioActivity().ROLE, RoleType.ADMIN.name)
                startActivity(intent)
            }
            R.id.nav_item_6 -> {
                val intent = Intent(this, ControlNoticiaActivity::class.java)
                intent.putExtra(USUARIOID, adminId)
                intent.putExtra(MenuAdministradorActivity().TOKEN, tokenAdmin)
                startActivity(intent)
                Toast.makeText(this, "Agregar Noticia", Toast.LENGTH_SHORT).show()
            }
//            R.id.nav_item_7 -> Toast.makeText(this, "Docentes Registrados", Toast.LENGTH_SHORT).show()
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
//            R.id.action_notificaciones -> {
//
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun actualizarNoticias(token: String){
        val adapter = NoticiaAdapter(this@MenuAdministradorActivity, ArrayList(), api, token, false)
        adapter.setAdminId(adminId)
        extraFuns.listarNoticias(api, adapter, this@MenuAdministradorActivity)
        recyclerView.adapter = adapter
    }
}