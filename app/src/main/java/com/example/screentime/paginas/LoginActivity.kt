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
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DBHelper(this)
        // CAMBIO: Usa EditText, que es el componente correcto para la entrada de texto.
        val nombreText = findViewById<EditText>(R.id.NombreText)
        val emailText = findViewById<EditText>(R.id.EmailText)
        val contrasenyaText = findViewById<EditText>(R.id.ContrasenyaText)
        val botonEntrar = findViewById<Button>(R.id.BotonEntrar)

        botonEntrar.setOnClickListener {
            // 1. Obtener y limpiar los datos de entrada.
            val nombre = nombreText.text.toString().trim()
            val email = emailText.text.toString().trim()
            val contrasenya = contrasenyaText.text.toString().trim()

            // --- CLÁUSULAS DE GUARDA (VALIDACIONES) ---

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
                Toast.makeText(this, "contraseña muy corta! Mínimo 8 caracteres!", Toast.LENGTH_LONG).show()
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
                val userFoto = cursor.getString(cursor.getColumnIndexOrThrow("fotoperfil"))

                val intent = Intent(this, HomeActivity::class.java).apply {
                    putExtra("userId", userId)
                    putExtra("userNombre", userNombre)
                    putExtra("userEmail", userEmail)
                    putExtra("userDesc", userDesc)
                    putExtra("userFoto", userFoto)
                }
                startActivity(intent)
                finish()
            } else {
                // Las validaciones de formato fueron correctas, pero el usuario no existe en la BD.
                Toast.makeText(this, "Usuario o contraseña incorrecta!", Toast.LENGTH_LONG).show()
            }

            cursor.close()
        }

    }
}