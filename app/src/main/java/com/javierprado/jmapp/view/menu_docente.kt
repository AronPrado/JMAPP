package com.javierprado.jmapp.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.login.LoginDocente
import com.javierprado.jmapp.view.login.OptionLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.javierprado.jmapp.data.entities.Noticia
class menu_docente : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toogle: ActionBarDrawerToggle

    private lateinit var auth: FirebaseAuth
    val TOKEN = "token"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_docente)

        //API Y BUNDLE
        val retro = RetrofitHelper.getInstanceStatic()
        val bundle = intent.extras
        if (bundle != null) {
            val token = bundle.getString(TOKEN, "")
            retro.setBearerToken(token)
        }
        val api = retro.getApi()

        auth = FirebaseAuth.getInstance()

        //Noticias
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewNews)
        recyclerView.layoutManager = LinearLayoutManager(this)

        var noticias : List<Noticia> = ArrayList()
        val adapter = NewsAdapter(noticias)
        var msg : String
        api.obtenerNoticias()?.enqueue(object :
            Callback<List<Noticia>> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<List<Noticia>>, response: Response<List<Noticia>>) {
                if (response.isSuccessful) {
                    noticias = response.body()!!
                    adapter.setNoticias(noticias);
                    adapter.notifyDataSetChanged();
                    msg = noticias.size.toString()
                }else{
                    msg = response.errorBody()?.string().toString()
                }
                Toast.makeText(this@menu_docente, msg, Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(call: Call<List<Noticia>>, t: Throwable?) {
                msg = "Error en el API"
                Toast.makeText(this@menu_docente, msg, Toast.LENGTH_SHORT).show()
            }
        })
        recyclerView.adapter = adapter


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
        when(item.itemId){
            R.id.nav_item_1 -> Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
            R.id.nav_item_2 -> Toast.makeText(this, "Seleccionar Seccion", Toast.LENGTH_SHORT).show()
            R.id.nav_item_3 -> Toast.makeText(this, "Ingresar notas", Toast.LENGTH_SHORT).show()
            R.id.nav_item_4 -> Toast.makeText(this, "Asistencia Escolar", Toast.LENGTH_SHORT).show()
            R.id.nav_item_5 -> Toast.makeText(this, "Programar Tareas y Evaluaciones", Toast.LENGTH_SHORT).show()
            R.id.nav_item_6 -> Toast.makeText(this, "Notificar Tareas", Toast.LENGTH_SHORT).show()
            R.id.nav_item_7 -> Toast.makeText(this, "Comunicarse con el apoderado", Toast.LENGTH_SHORT).show()
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
}