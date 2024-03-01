package com.javierprado.jmapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.view.login.LoginAdmin
import com.javierprado.jmapp.view.login.OptionLogin

class menu_administrador : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toogle: ActionBarDrawerToggle

    private lateinit var auth: FirebaseAuth
    val TOKEN = "token"
    var tokenAdmin = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_administrador)

        //API Y BUNDLE
        val retro = RetrofitHelper.getInstanceStatic()
        val bundle = intent.extras
        if (bundle != null) {
            val token = bundle.getString(TOKEN, "")
            tokenAdmin=token
            retro.setBearerToken(tokenAdmin)
        }
        val api = retro.getApi()

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
                Toast.makeText(this, "Redactar y Enviar Notificaciones", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, activity_register_student::class.java)
                intent.putExtra(activity_register_student().TOKEN, tokenAdmin)
                startActivity(intent)
            }
            R.id.nav_item_3 -> {
                Toast.makeText(this, "Redactar y Enviar Notificaciones", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, NotiEventosEsco::class.java)
                startActivity(intent)
            }
            R.id.nav_item_4 -> Toast.makeText(this, "Editar Horario Escolar", Toast.LENGTH_SHORT).show()
            R.id.nav_item_5 -> Toast.makeText(this, "Editar Tablero de Noticias", Toast.LENGTH_SHORT).show()
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

}