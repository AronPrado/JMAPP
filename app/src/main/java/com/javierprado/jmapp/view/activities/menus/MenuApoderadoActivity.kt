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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.entities.Estudiante
import com.javierprado.jmapp.view.adapters.NoticiaAdapter
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.data.util.ExtraFunctions
import com.javierprado.jmapp.data.util.NavigationWindows
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.mvvm.ChatAppViewModel
import com.javierprado.jmapp.view.activities.comunicacion.ChatDocenteApoderadoActivity
import com.javierprado.jmapp.view.activities.control.ControlEstudianteActivity
import com.javierprado.jmapp.view.activities.control.ControlHorarioActivity
import com.javierprado.jmapp.view.activities.editar.ActualizarInfoApoderadoActivity
import com.javierprado.jmapp.view.login.OptionLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class MenuApoderadoActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object{
        lateinit var instance : MenuApoderadoActivity
    }
    lateinit var viewModel: ChatAppViewModel
    private lateinit var drawer:DrawerLayout
    private lateinit var toogle: ActionBarDrawerToggle

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnMasRecientes: ImageView

    private lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    private lateinit var api : ColegioAPI

    private var extraFuns : ExtraFunctions = ExtraFunctions()

    val HIJOS = "hijos"
    var tokenApod = ""; var apoderadoId = "" ; var hijos: List<Estudiante> = ArrayList()
    private lateinit var msg : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_apoderado)

        viewModel = ViewModelProvider(this)[ChatAppViewModel::class.java]

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        btnMasRecientes = findViewById(R.id.btn_mas_reciente)
        //API Y BUNDLE
        val retro = RetrofitHelper.getInstanceStatic()
        val bundle = intent.extras
        if (bundle != null) {
            apoderadoId = bundle.getString(MenuAdministradorActivity().USUARIOID, "")
            tokenApod = bundle.getString(MenuAdministradorActivity().TOKEN, "")
            retro.setBearerToken(tokenApod)
        }
        api = retro.getApi()
        //Noticias
        recyclerView = findViewById(R.id.recyclerViewNews)
        recyclerView.layoutManager = LinearLayoutManager(this)

        actualizarNoticias(retro.getBearerToken())
        api.listarEstudiantes(apoderadoId, ArrayList()).enqueue(object : Callback<List<Estudiante>> {
            override fun onResponse(call: Call<List<Estudiante>>, response: Response<List<Estudiante>>) {
                msg = response.headers()["message"] ?: ""
                if (response.isSuccessful) {
                    hijos = response.body()!!
                } else{
                    Toast.makeText(this@MenuApoderadoActivity, msg, Toast.LENGTH_SHORT).show()
                    Log.e("L ESTUDIANTES", "")
                }
            }
            override fun onFailure(call: Call<List<Estudiante>>, t: Throwable) {
                msg = "Error en la API: ${t.message}"
                Log.e("ESTUDIANTES", t.message.toString())
                Toast.makeText(this@MenuApoderadoActivity, msg, Toast.LENGTH_SHORT).show()
            }
        } )
        btnMasRecientes.setOnClickListener { actualizarNoticias(retro.getBearerToken()) }

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)

        toogle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawer.addDrawerListener(toogle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view_apoderado)
        navigationView.setNavigationItemSelectedListener(this)
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_item_1 -> Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
            R.id.nav_item_2 -> Toast.makeText(this, "Trabajos", Toast.LENGTH_SHORT).show()
            R.id.nav_item_3 -> Toast.makeText(this, "Recursos", Toast.LENGTH_SHORT).show()
            R.id.nav_item_4 -> {
                val intent = Intent(this, ControlHorarioActivity::class.java)
                intent.putExtra(MenuAdministradorActivity().USUARIOID, apoderadoId)
                intent.putExtra(MenuAdministradorActivity().TOKEN, tokenApod)
                intent.putExtra(ControlHorarioActivity().ESTUDIANTES, hijos as Serializable)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent) ; finish()
                Toast.makeText(this@MenuApoderadoActivity, "Horario", Toast.LENGTH_SHORT).show()

            }
            R.id.nav_item_5 -> {
                val intent = Intent(this, ControlEstudianteActivity::class.java)
                intent.putExtra(ControlEstudianteActivity().DIRECT, NavigationWindows.REUNIONES.name)
                intent.putExtra(MenuAdministradorActivity().USUARIOID, apoderadoId)
                intent.putExtra(MenuAdministradorActivity().TOKEN, tokenApod)
                intent.putExtra(HIJOS, hijos as Serializable)
                startActivity(intent)
                Toast.makeText(this, "Reuniones", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_item_6 -> Toast.makeText(this, "Docentes", Toast.LENGTH_SHORT).show()
            R.id.nav_item_7 -> {
                Toast.makeText(this, "Comunicación", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ChatDocenteApoderadoActivity::class.java)
                firestore.collection("Users").document(AnotherUtil.getUidLoggedIn()).update("token", tokenApod)
                startActivity(intent)
            }
            R.id.nav_item_8 -> {
                auth.signOut()
                val intent = Intent(this, OptionLogin::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.perfil_noti_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_perfil -> {
                val intent = Intent(this, ActualizarInfoApoderadoActivity::class.java)
                intent.putExtra(MenuAdministradorActivity().USUARIOID, apoderadoId)
                intent.putExtra(MenuAdministradorActivity().TOKEN, tokenApod)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                true
            }
//            R.id.action_notificaciones -> {
//                // Maneja la acción de Búsqueda
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun actualizarNoticias(token: String){
        val adapter = NoticiaAdapter(this@MenuApoderadoActivity, ArrayList(), api, token, true)
        extraFuns.listarNoticias(api, adapter, this@MenuApoderadoActivity)
        recyclerView.adapter = adapter
    }
}