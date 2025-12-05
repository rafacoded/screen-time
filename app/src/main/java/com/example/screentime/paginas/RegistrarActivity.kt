package com.example.screentime.paginas

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.screentime.R
import com.example.screentime.database.DBHelper
import de.hdodenhof.circleimageview.CircleImageView

class RegistrarActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var imagenPerfil: CircleImageView
    private var imagenByteArray: ByteArray? = null // Variable para guardar la URI de la imagen seleccionada

    private val selectorDeImagen = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imagenPerfil.setImageURI(it)
            contentResolver.openInputStream(it)?.use { inputStream ->
                imagenByteArray = inputStream.readBytes()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        dbHelper = DBHelper(this)

        imagenPerfil = findViewById(R.id.select_photo_imageview_register)
        imagenPerfil.setOnClickListener {
            // Abrimos la galería de imágenes del dispositivo
            selectorDeImagen.launch("image/*")
        }
        val nombreEditText = findViewById<EditText>(R.id.NombreReg)
        val emailEditText = findViewById<EditText>(R.id.EmailReg)
        val contrasenyaEditText = findViewById<EditText>(R.id.ContrasenyaReg)

        val botonRegistrar = findViewById<Button>(R.id.btnRegistrar)
        botonRegistrar.setOnClickListener {
            val nombre = nombreEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val contrasenya = contrasenyaEditText.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || contrasenya.isEmpty()) {
                Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Usamos la nueva variable imagenByteArray
            val exito = dbHelper.insertUsuario(nombre, "", contrasenya, email, imagenByteArray)

            if (exito != -1L) {
                Toast.makeText(this, "Usuario '$nombre' registrado con éxito", Toast.LENGTH_LONG).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al registrar. El email puede que ya exista.", Toast.LENGTH_LONG).show()
            }
        }

    }
}