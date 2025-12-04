package com.example.screentime
import com.example.screentime.paginas.LoginActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.screentime.paginas.HomeActivity
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val botonAcceso = findViewById<Button>(R.id.btnAcceder)

        botonAcceso.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }
}