package com.example.screentime.paginas

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.screentime.MainActivity
import com.example.screentime.R
import com.example.screentime.fragments.BuscarFragment
import com.example.screentime.fragments.CalendarioFragment
import com.example.screentime.fragments.HomeFragment
import com.example.screentime.session.SessionManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class InicioActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val buscarFragment = BuscarFragment()
    private val calendarioFragment = CalendarioFragment()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        openFragment(homeFragment)

        // SESSION MANAGER (gestionar los atributos de user para el intent)
        val session = SessionManager(this)
        val userId = session.getUserId()
        val userNombre = session.getUserName()
        val userEmail = session.getUserEmail()
        val userDesc = session.getUserDesc()

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navigationView)

        val bottomNavBar = findViewById<BottomNavigationView>(R.id.bottomNav)

        val userFoto = intent.getStringExtra("userFoto")

        val headerView = navView.getHeaderView(0)
        val headerName = headerView.findViewById<TextView>(R.id.headerName)
        val headerImage = headerView.findViewById<ImageView>(R.id.headerImage)

        // Asignar los valores a las vistas del header
        headerName.text = session.getUserName()
        Glide.with(this).load(userFoto).into(headerImage)

        // DRAWER NAVIGATION
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_perfil -> { }
                R.id.nav_configuracion -> { }
                R.id.nav_sobre -> { }
                R.id.nav_logout -> {
                    session.clear()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // --- BOTTOM NAVIGATION ---
        bottomNavBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> openFragment(homeFragment)
                R.id.nav_buscar -> openFragment(buscarFragment)
                R.id.nav_calendar -> openFragment(calendarioFragment)
            }
            true
        }

    }
    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}