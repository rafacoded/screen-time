package com.example.screentime.paginas

import com.example.screentime.database.DBHelper
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText // CAMBIO: Usar EditText para los campos de entrada
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.screentime.R
import com.example.screentime.session.SessionManager
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DBHelper(this)

        val nombreText = findViewById<EditText>(R.id.NombreText)
        val emailText = findViewById<EditText>(R.id.EmailText)
        val contrasenyaText = findViewById<EditText>(R.id.ContrasenyaText)
        val botonEntrar = findViewById<Button>(R.id.BotonEntrar)

        botonEntrar.setOnClickListener {

            val nombre = nombreText.text.toString().trim()
            val email = emailText.text.toString().trim()
            val contrasenya = contrasenyaText.text.toString().trim()

            if (nombre.isEmpty() || nombre.length > 50) {
                Toast.makeText(this, "Nombre vacío o muy largo!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val p = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
            if (!p.matcher(email).matches()) {
                Toast.makeText(this, "Email inválido!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (contrasenya.length < 8) {
                Toast.makeText(this, "Contraseña muy corta! Mínimo 8 caracteres!", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val cursor = dbHelper.readableDatabase.rawQuery(
                "SELECT * FROM usuario WHERE nombre = ? AND email = ? AND contrasenya = ?",
                arrayOf(nombre, email, contrasenya)
            )

            if (cursor.moveToFirst()) {
                Toast.makeText(this, "Bienvenido $nombre", Toast.LENGTH_LONG).show()

                val userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val userNombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val userEmail = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val userDesc = cursor.getString(cursor.getColumnIndexOrThrow("descripcion"))

                val session = SessionManager(this)

                session.saveUserId(userId)
                session.saveUserName(userNombre)
                session.saveUserEmail(userEmail)
                session.saveUserDesc(userDesc)

                startActivity(Intent(this, InicioActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrecta!", Toast.LENGTH_LONG).show()
            }

            cursor.close()
        }

    }
}