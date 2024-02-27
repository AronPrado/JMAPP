package com.javierprado.jmapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.javierprado.jmapp.R
import com.javierprado.jmapp.clases.NewsAdapter
import com.javierprado.jmapp.clases.Noticia
import com.javierprado.jmapp.view.login.LoginActivity
import com.javierprado.jmapp.view.login.LoginAdmin
import com.javierprado.jmapp.view.login.OptionLogin

class menu_apoderado : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer:DrawerLayout
    private lateinit var toogle: ActionBarDrawerToggle

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_apoderado)

        //Noticias
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewNews)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val noticias = obtenerListaDeNoticias()
        val adapter = NewsAdapter(noticias)
        recyclerView.adapter = adapter

        auth = FirebaseAuth.getInstance()


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

    //Noticias
    private fun obtenerListaDeNoticias(): List<Noticia> {

        return listOf(
            Noticia("Título 1", "Descripción 1", "Autor 1", "Fecha 1", R.drawable.task),
            Noticia("Título 2", "Descripción 2", "Autor 2", "Fecha 2", R.drawable.task),
            Noticia("Título 1", "Descripción 1", "Autor 1", "Fecha 1", R.drawable.task),
            Noticia("Título 2", "Descripción 2", "Autor 2", "Fecha 2", R.drawable.task),
            Noticia("Título 1", "Descripción 1", "Autor 1", "Fecha 1", R.drawable.task),
            Noticia("Título 2", "Descripción 2", "Autor 2", "Fecha 2", R.drawable.task),
        )
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_item_1 -> Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
            R.id.nav_item_2 -> Toast.makeText(this, "Trabajos", Toast.LENGTH_SHORT).show()
            R.id.nav_item_3 -> Toast.makeText(this, "Recursos", Toast.LENGTH_SHORT).show()
            R.id.nav_item_4 -> Toast.makeText(this, "Horario", Toast.LENGTH_SHORT).show()
            R.id.nav_item_5 -> Toast.makeText(this, "Eventos", Toast.LENGTH_SHORT).show()
            R.id.nav_item_6 -> Toast.makeText(this, "Docentes", Toast.LENGTH_SHORT).show()
            R.id.nav_item_7 -> Toast.makeText(this, "Comunicación", Toast.LENGTH_SHORT).show()
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
                val intent = Intent(this, activity_register_student::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
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