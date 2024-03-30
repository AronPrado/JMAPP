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
import com.google.firebase.firestore.FirebaseFirestore
import com.javierprado.jmapp.R
import com.javierprado.jmapp.model.NewsAdapter
import com.javierprado.jmapp.data.entities.Usuario
import com.javierprado.jmapp.data.retrofit.ColegioAPI
import com.javierprado.jmapp.data.retrofit.RetrofitHelper
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.data.util.ExtraFunctions
import com.javierprado.jmapp.data.util.RoleType
import com.javierprado.jmapp.view.activities.comunicacion.ChatDocenteApoderadoActivity
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
    private lateinit var drawer:DrawerLayout
    private lateinit var toogle: ActionBarDrawerToggle

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnMasRecientes: ImageView

    private lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    private lateinit var api : ColegioAPI

    private var extraFuns : ExtraFunctions = ExtraFunctions()

    val TOKEN = "token"
    var tokenApod = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_apoderado)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        btnMasRecientes = findViewById(R.id.btn_mas_reciente)
        //API Y BUNDLE
        val retro = RetrofitHelper.getInstanceStatic()
        val bundle = intent.extras
        if (bundle != null) {
            val token = bundle.getString(TOKEN, "")
            tokenApod=token
            retro.setBearerToken(token)
        }
        api = retro.getApi()
        //BORRAR
//        val user = auth.currentUser
//        if(user!=null){
//            val dataHashMap = hashMapOf("userid" to user.uid, "info" to "Benjamín Carlos Apaza Enriquez", "correo" to "bnhcks22@gmail.com", "estado" to "default", "tipo" to "APOD", "tipoid" to "6", "token" to "")
//            firestore.collection("Users").document(user.uid).set(dataHashMap).addOnCompleteListener {
//                    task -> Log.e("ERROR FSTORE", task.exception.toString()) }
//        }
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
                intent.putExtra(ControlHorarioActivity().TOKEN, tokenApod)
                intent.putExtra(ControlHorarioActivity().ROLE, RoleType.APOD)
                var msg = ""
                api.obtenerSesion(RoleType.APOD.name).enqueue(object : Callback<Usuario> {
                    override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                        msg = "Usuario no encontrado."
                        if (response.isSuccessful) {
                            val usuario = response.body()!!
                            val apoderado = usuario.apoderado
                            val estudiantes = apoderado.itemsEstudiante!!.toList()
                            intent.putExtra(ControlHorarioActivity().ESTUDIANTES, estudiantes as Serializable)
                            startActivity(intent)
                        } else{
                            Log.e("NR SESSION", msg)
                            Toast.makeText(this@MenuApoderadoActivity, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Usuario>, t: Throwable) {
                        msg = "Error en la API: ${t.message}"
                        Toast.makeText(this@MenuApoderadoActivity, msg, Toast.LENGTH_SHORT).show()
                        Log.e("BUSCAR SESSION", t.message.toString())
                    }
                } )
                Toast.makeText(this@MenuApoderadoActivity, "Horario", Toast.LENGTH_SHORT).show()

            }
            R.id.nav_item_5 -> Toast.makeText(this, "Eventos", Toast.LENGTH_SHORT).show()
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
    fun actualizarNoticias(token: String){
        val adapter = NewsAdapter(this@MenuApoderadoActivity, ArrayList(), api, token, true)
        extraFuns.listarNoticias(api, adapter, this@MenuApoderadoActivity)
        recyclerView.adapter = adapter
    }
}