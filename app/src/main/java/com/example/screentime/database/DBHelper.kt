package com.example.screentime.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.screentime.models.Pelicula

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "screenTime.db"
        private const val DATABASE_VERSION = 4
        const val TABLE_PELICULA = "pelicula"
        const val TABLE_RESENYA = "resenya"
        const val TABLE_USUARIO = "usuario"
        const val TABLE_RECORDATORIO = "recordatorio"
        const val TABLE_PELICULAUSUARIO = "peliculausuario"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // --- CREACIÓN DE TABLAS ---
        // Se crean primero las tablas que NO tienen claves foráneas (FOREIGN KEYs).

        val createUsuarioTable = """
            CREATE TABLE $TABLE_USUARIO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre VARCHAR(60),
                descripcion VARCHAR(500),
                contrasenya VARCHAR(50),
                email VARCHAR(100) UNIQUE,
                fotoperfil VARCHAR(256)
            )
        """
        db.execSQL(createUsuarioTable)

        val createPeliculaTable = """
            CREATE TABLE $TABLE_PELICULA (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                fechasalida VARCHAR(50),
                genero VARCHAR(30),
                nombre VARCHAR(60),
                sinopsis VARCHAR(500),
                estado VARCHAR(20),
                foto VARCHAR(255)
            )
        """
        db.execSQL(createPeliculaTable)

        // DATOS DEMO PELICULA
        insertarPeliculasDemo(db)

        val createResenyaTable = """
            CREATE TABLE $TABLE_RESENYA (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                descripcion VARCHAR(500),
                calificacion INTEGER,
                fecha VARCHAR(50),
                id_pelicula INTEGER,
                id_usuario INTEGER,
                FOREIGN KEY (id_pelicula) REFERENCES $TABLE_PELICULA(id) ON DELETE CASCADE,
                FOREIGN KEY (id_usuario) REFERENCES $TABLE_USUARIO(id) ON DELETE CASCADE
            )
        """
        db.execSQL(createResenyaTable)

        // Crear tabla usuario

        val createRecordatorioTable = """
            CREATE TABLE $TABLE_RECORDATORIO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre VARCHAR(60),
                descripcion VARCHAR(300),
                fecha VARCHAR(50),
                id_pelicula INTEGER,
                id_usuario INTEGER,
                FOREIGN KEY (id_pelicula) REFERENCES $TABLE_PELICULA(id) ON DELETE CASCADE,
                FOREIGN KEY (id_usuario) REFERENCES $TABLE_USUARIO(id) ON DELETE CASCADE
            )
        """
        db.execSQL(createRecordatorioTable)

        val createPeliculaUsuarioTable = """
            CREATE TABLE $TABLE_PELICULAUSUARIO (
                id_pelicula INTEGER,
                id_usuario INTEGER,
                PRIMARY KEY (id_pelicula, id_usuario),
                FOREIGN KEY (id_pelicula) REFERENCES $TABLE_PELICULA(id) ON DELETE CASCADE,
                FOREIGN KEY (id_usuario) REFERENCES $TABLE_USUARIO(id) ON DELETE CASCADE
            )
        """
        db.execSQL(createPeliculaUsuarioTable)

        // --- INSERCIÓN DE DATOS INICIALES ---
        // ¡Crucial para que el Login funcione desde el principio!
        db.execSQL("""
            INSERT INTO $TABLE_USUARIO (nombre, email, contrasenya) VALUES
            ('Jaime', 'jfuertesgarcia@safareyes.es', '123456789'),
            ('Rafael', 'rtiradoheras@safareyes.es', 'abcdefghijk');
        """)
    }

    /**
     * Se ejecuta cuando el número de DATABASE_VERSION en el código es MAYOR
     * que el número de versión de la BD instalada en el móvil.
     * Ideal para modificar la estructura de tablas sin que los usuarios pierdan sus datos.
     * Para desarrollo, la forma más simple es borrar todo y crearlo de nuevo.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // NO HACER DROPS, SINO LOS DATOS NO PERSISTEN AL CERRARLA Y ABRIRLA
    }
    // --------------------- PELICULA ---------------------

    fun insertPelicula(nombre: String,
                       genero: String?,
                       fechasalida: String?,
                       sinopsis: String?,
                       estado: String?,
                       foto: String?): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("genero", genero)
            put("fechasalida", fechasalida)
            put("sinopsis", sinopsis)
            put("estado", estado)
            put("foto",foto)
        }
        return db.insert(TABLE_PELICULA, null, values)
    }

    fun getPeliculaById(id: Int): Pelicula? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PELICULA WHERE id = ?", arrayOf(id.toString()))

        return if (cursor.moveToFirst()) {
            val pelicula = Pelicula(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                genero = cursor.getString(cursor.getColumnIndexOrThrow("genero")),
                fechasalida = cursor.getString(cursor.getColumnIndexOrThrow("fechasalida")),
                sinopsis = cursor.getString(cursor.getColumnIndexOrThrow("sinopsis")),
                estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                foto = cursor.getString(cursor.getColumnIndexOrThrow("foto"))
            )
            cursor.close()
            pelicula
        } else {
            cursor.close()
            null
        }
    }

    fun getPeliculasPendientes(): List<Pelicula> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM pelicula WHERE estado = 'pendiente'", null)
        return cursorToList(cursor)
    }

    fun getPeliculasVistas(): List<Pelicula> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM pelicula WHERE estado = 'vista'", null)
        return cursorToList(cursor)
    }

    fun getAllPeliculas(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_PELICULA", null)
    }

    fun getAllPeliculasList(): List<Pelicula> {
        val cursor = readableDatabase.rawQuery("SELECT * FROM pelicula", null)
        return cursorToList(cursor)
    }

    fun updatePelicula(
        id: Int,
        nombre: String,
        genero: String?,
        fechasalida: String?,
        sinopsis: String?,
        estado: String?,
        foto: String
    ): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("genero", genero)
            put("fechasalida", fechasalida)
            put("sinopsis", sinopsis)
            put("estado", estado)
            put("foto", foto)
        }
        return db.update(TABLE_PELICULA, values, "id = ?", arrayOf(id.toString()))
    }

    fun deletePelicula(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_PELICULA, "id = ?", arrayOf(id.toString()))
    }

    // --------------------- Reseña ---------------------
    fun insertResenya (
        descripcion: String,
        calificacion: Int,
        fecha: String,
        id_pelicula: Int,
        id_usuario: Int
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("descripcion", descripcion)
            put("calificacion", calificacion)
            put("fecha", fecha)
            put("id_pelicula", id_pelicula)
            put("id_usuario", id_usuario)
        }
        return db.insert(TABLE_RESENYA, null, values)
    }
    fun getAllResenyas(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_RESENYA", null)
    }
    fun updateResenya(
        id: Int,
        descripcion: String,
        calificacion: Int,
        fecha: String,
        id_pelicula: Int,
        id_usuario: Int
    ): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("descripcion",descripcion)
            put("calificacion",calificacion)
            put("fecha",fecha)
            put("id_pelicula",id_pelicula)
            put("id_usuario", id_usuario)
        }
        return db.update(TABLE_RESENYA, values, "id = ?", arrayOf(id.toString()))
    }
    fun deleteResenya(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_RESENYA, "id = ?", arrayOf(id.toString()))
    }

    // --------------------- USUARIO ---------------------
    fun insertUsuario(
        nombre: String,
        descripcion: String?,
        contrasenya: String?,
        email: String?,
        fotoperfil: String?
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("contrasenya", contrasenya)
            put("email", email)
            put("fotoperfil", fotoperfil)
        }
        return db.insert(TABLE_USUARIO, null, values)
    }

    fun getAllUsuarios(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_USUARIO", null)
    }

    fun updateUsuario(
        id: Int,
        nombre: String,
        descripcion: String?,
        contrasenya: String?,
        email: String?,
        fotoperfil: String?
    ): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("contrasenya", contrasenya)
            put("email", email)
            put("fotoperfil", fotoperfil)
        }
        return db.update(TABLE_USUARIO, values, "id = ?", arrayOf(id.toString()))
    }

    fun deleteUsuario(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_USUARIO, "id = ?", arrayOf(id.toString()))
    }

    // --------------------- Recordatorio ---------------------
    fun insertRecordatorio(
        nombre: String,
        descripcion: String?,
        fecha: String,
        id_pelicula: Int?,
        id_usuario: Int?
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("fecha", fecha)
            if (id_pelicula != null) put("id_pelicula", id_pelicula)
            if (id_usuario != null) put("id_usuario", id_usuario)
        }
        return db.insert(TABLE_RECORDATORIO, null, values)
    }

    fun getAllRecordatorios(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_RECORDATORIO", null)
    }

    fun updateRecordatorio(
        id: Int,
        nombre: String,
        descripcion: String?,
        fecha: String,
        id_pelicula: Int?,
        id_usuario: Int?
    ): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("fecha", fecha)
            if (id_pelicula != null) put("id_pelicula", id_pelicula)
            if (id_usuario != null) put("id_usuario", id_usuario)
        }
        return db.update(TABLE_RECORDATORIO, values, "id = ?", arrayOf(id.toString()))
    }

    fun deleteRecordatorio(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_RECORDATORIO, "id = ?", arrayOf(id.toString()))
    }

    // --------------------- PeliculaUsuario ---------------------
    fun insertPeliculaUsuario(id_pelicula: Int, id_usuario: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("id_pelicula", id_pelicula)
            put("id_usuario", id_usuario)
        }
        return db.insert(TABLE_PELICULAUSUARIO, null, values)
    }

    fun getAllPeliculasUsuario(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_PELICULAUSUARIO", null)
    }

    fun deletePeliculaUsuario(id_pelicula: Int, id_usuario: Int): Int {
        val db = this.writableDatabase
        return db.delete(
            TABLE_PELICULAUSUARIO,
            "id_pelicula = ? AND id_usuario = ?",
            arrayOf(id_pelicula.toString(), id_usuario.toString())
        )
    }

    // DATOS DEMO
    private fun insertarPeliculasDemo(db: SQLiteDatabase) {

        // Insertar datos demo
        db.execSQL("""
            INSERT INTO pelicula (fechasalida, genero, nombre, sinopsis, estado, foto)
            VALUES ('2014-11-07', 'Ciencia ficción', 'Interstellar', 
            'Un grupo de astronautas viaja a través...', 'vista',
            'https://mir-s3-cdn-cf.behance.net/project_modules/hd_webp/8d8f28105415493.619ded067937d.jpg')
        """)

        db.execSQL("""
            INSERT INTO pelicula (fechasalida, genero, nombre, sinopsis, estado, foto)
            VALUES ('2021-12-17', 'Acción', 'Spider-Man: No Way Home',
            'Peter Parker abre puertas del multiverso...', 'pendiente',
            'https://images-cdn.ubuy.co.in/633b488f75139c0bdc5db98a-rock-poster-tom-holland-spider-man-3-no.jpg')
        """)

        db.execSQL("""
            INSERT INTO pelicula (fechasalida, genero, nombre, sinopsis, estado, foto)
            VALUES ('2022-03-04', 'Acción', 'The Batman',
            'Batman investiga una serie de crímenes cometidos por Enigma, revelando corrupción en Gotham.', 'vista',
            'https://m.media-amazon.com/images/I/61xG1mnV7aL._AC_UF1000,1000_QL80_.jpg')
        """)

        db.execSQL("""
            INSERT INTO pelicula (fechasalida, genero, nombre, sinopsis, estado, foto)
            VALUES ('2001-12-19', 'Fantasía', 'El Señor de los Anillos: La Comunidad del Anillo',
            'Frodo inicia su viaje para destruir el Anillo Único con la ayuda de la Comunidad del Anillo.', 'pendiente',
            'https://resizing.flixster.com/-XZAfHZM39UwaGJIFWKAE8fS0ak=/v3/t/assets/p28828_p_v8_ao.jpg')
        """)

        db.execSQL("""
            INSERT INTO pelicula (fechasalida, genero, nombre, sinopsis, estado, foto)
            VALUES ('2016-12-09', 'Musical', 'La La Land',
            'Una actriz y un músico intentan cumplir sus sueños en Los Ángeles mientras luchan con su relación.', 'vista',
            'https://images.store.sky.com/api/img/asset/en/66D8BB8A-E4E8-4422-9242-603110084545_5A41DEE1-191E-4086-95D4-509F4614DE01_2025-4-23-T11-33-16.jpg?s=260x371')
        """)

    }


    // UTILIDADES
    private fun cursorToList(cursor: Cursor): List<Pelicula> {
        val lista = mutableListOf<Pelicula>()

        if (cursor.moveToFirst()) {
            do {
                val pelicula = Pelicula(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    genero = cursor.getString(cursor.getColumnIndexOrThrow("genero")),
                    fechasalida = cursor.getString(cursor.getColumnIndexOrThrow("fechasalida")),
                    sinopsis = cursor.getString(cursor.getColumnIndexOrThrow("sinopsis")),
                    estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                    foto = cursor.getString(cursor.getColumnIndexOrThrow("foto"))
                )
                lista.add(pelicula)
            } while (cursor.moveToNext())
        }

        cursor.close()

        return lista
    }


}