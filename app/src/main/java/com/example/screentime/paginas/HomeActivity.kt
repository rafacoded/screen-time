package com.example.screentime.paginas

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.screentime.R
import com.example.screentime.utils.MyPagerAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navigationView)

        // Corregido: Obtener los datos del Intent con las claves correctas
        val userId = intent.getIntExtra("userId", -1)
        val userNombre = intent.getStringExtra("userNombre")
        val userEmail = intent.getStringExtra("userEmail")
        val userDesc = intent.getStringExtra("userDesc")
        val userFoto = intent.getStringExtra("userFoto")

        val headerView = navView.getHeaderView(0)
        val headerName = headerView.findViewById<TextView>(R.id.headerName)
        val headerImage = headerView.findViewById<ImageView>(R.id.headerImage)

        // Asignar los valores a las vistas del header
        headerName.text = userNombre
        Glide.with(this).load(userFoto).into(headerImage)

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_perfil -> {
                    // Ir a activity: Perfil
                }
                R.id.nav_configuracion -> {
                    // Ir a activity: Configuración
                }
                R.id.nav_sobre -> {
                    // Ir a activity: Sobre Nosotros
                }
                R.id.nav_logout -> {
                    // cerrar sesión
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val adapter = MyPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Pendientes" else "Vistas"
        }.attach()
    }
}
