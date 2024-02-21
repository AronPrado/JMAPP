package com.javierprado.jmapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.javierprado.jmapp.R

class menu_apoderado : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer:DrawerLayout
    private lateinit var toogle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_apoderado)


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
            R.id.nav_item_4 -> Toast.makeText(this, "Horario", Toast.LENGTH_SHORT).show()
            R.id.nav_item_5 -> Toast.makeText(this, "Eventos", Toast.LENGTH_SHORT).show()
            R.id.nav_item_6 -> Toast.makeText(this, "Docentes", Toast.LENGTH_SHORT).show()
            R.id.nav_item_7 -> Toast.makeText(this, "Comunicación", Toast.LENGTH_SHORT).show()
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