import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.screentime.models.Pelicula
import java.util.Date

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "screenTime.db"
        private const val DATABASE_VERSION = 1

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
        // Crear tabla pelicula
        val createPelicula = """
            CREATE TABLE $TABLE_PELICULA (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                fechasalida TEXT,
                genero TEXT,
                nombre TEXT,
                sinopsis TEXT,
                emitida INTEGER DEFAULT 0,
                estado TEXT,
                foto TEXT
            )
        """
        db.execSQL(createPelicula)

        // Insertar datos demo
        db.execSQL("""
            INSERT INTO pelicula (fechasalida, genero, nombre, sinopsis, emitida, estado, foto)
            VALUES ('2014-11-07', 'Ciencia ficción', 'Interstellar', 
            'Un grupo de astronautas viaja a través...', 1, 'vista',
            'https://cartelera.laverdad.es/img/carteles/2014/11/interestellar__310x443.jpg')
        """)

        db.execSQL("""
            INSERT INTO pelicula (fechasalida, genero, nombre, sinopsis, emitida, estado, foto)
            VALUES ('2021-12-17', 'Acción', 'Spider-Man: No Way Home',
            'Peter Parker abre puertas del multiverso...', 1, 'pendiente',
            'https://pics.filmaffinity.com/Spider_Man_No_Way_Home-387287198-large.jpg')
        """)

        db.execSQL("""
            INSERT INTO pelicula (fechasalida, genero, nombre, sinopsis, emitida, estado, foto)
            VALUES ('2022-03-04', 'Acción', 'The Batman',
            'Batman investiga una serie de crímenes cometidos por Enigma, revelando corrupción en Gotham.', 1, 'vista',
            'https://m.media-amazon.com/images/I/61xG1mnV7aL._AC_UF1000,1000_QL80_.jpg')
        """)

        db.execSQL("""
            INSERT INTO pelicula (fechasalida, genero, nombre, sinopsis, emitida, estado, foto)
            VALUES ('2001-12-19', 'Fantasía', 'El Señor de los Anillos: La Comunidad del Anillo',
            'Frodo inicia su viaje para destruir el Anillo Único con la ayuda de la Comunidad del Anillo.', 1, 'pendiente',
            'https://resizing.flixster.com/-XZAfHZM39UwaGJIFWKAE8fS0ak=/v3/t/assets/p28828_p_v8_ao.jpg')
        """)

        db.execSQL("""
            INSERT INTO pelicula (fechasalida, genero, nombre, sinopsis, emitida, estado, foto)
            VALUES ('2016-12-09', 'Musical', 'La La Land',
            'Una actriz y un músico intentan cumplir sus sueños en Los Ángeles mientras luchan con su relación.', 1, 'vista',
            'https://images.store.sky.com/api/img/asset/en/66D8BB8A-E4E8-4422-9242-603110084545_5A41DEE1-191E-4086-95D4-509F4614DE01_2025-4-23-T11-33-16.jpg?s=260x371')
        """)

        // Crear tabla resenya
        val createResenya = """
            CREATE TABLE $TABLE_RESENYA (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                descripcion TEXT,
                calificacion INTEGER,
                fecha TEXT,
                id_pelicula INTEGER,
                id_usuario INTEGER,
                FOREIGN KEY (id_pelicula) REFERENCES $TABLE_PELICULA(id),
                FOREIGN KEY (id_usuario) REFERENCES $TABLE_USUARIO(id)
            )
        """
        db.execSQL(createResenya)

        // Crear tabla usuario
        val createUsuario = """
            CREATE TABLE $TABLE_USUARIO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                descripcion TEXT,
                contraseña TEXT,
                email TEXT,
                fotoperfil TEXT,
                id_resenya INTEGER,
                FOREIGN KEY (id_resenya) REFERENCES $TABLE_RESENYA(id)
            )
        """.trimIndent()
        db.execSQL(createUsuario)

        // Crear tabla recordatorio
        val createRecordatorio = """
            CREATE TABLE $TABLE_RECORDATORIO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                descripcion TEXT,
                fecha TEXT,
                id_pelicula INTEGER,
                id_usuario INTEGER,
                FOREIGN KEY (id_pelicula) REFERENCES $TABLE_PELICULA(id),
                FOREIGN KEY (id_usuario) REFERENCES $TABLE_USUARIO(id)
            )
        """
        db.execSQL(createRecordatorio)

        // Crear tabla peliculausuario
        val createPeliculaUsuario = """
            CREATE TABLE $TABLE_PELICULAUSUARIO (
                id_pelicula INTEGER,
                id_usuario INTEGER,
                PRIMARY KEY (id_pelicula, id_usuario),
                FOREIGN KEY (id_pelicula) REFERENCES $TABLE_PELICULA(id),
                FOREIGN KEY (id_usuario) REFERENCES $TABLE_USUARIO(id)
            )
        """
        db.execSQL(createPeliculaUsuario)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PELICULAUSUARIO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RECORDATORIO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RESENYA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PELICULA")
        onCreate(db)
    }
    // --------------------- PELICULA ---------------------

    fun insertPelicula(nombre: String,
                       genero: String?,
                       fechasalida: String?,
                       sinopsis: String?,
                       emitida: Boolean,
                       estado: String?,
                       foto: String?): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("genero", genero)
            put("fechasalida", fechasalida)
            put("sinopsis", sinopsis)
            put("emitida", if (emitida) 1 else 0)
            put("foto",foto)
            put("estado", estado)
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
                emitida = cursor.getInt(cursor.getColumnIndexOrThrow("emitida")) == 1,
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

    fun updatePelicula(
        id: Int,
        nombre: String,
        genero: String?,
        fechasalida: String?,
        sinopsis: String?,
        emitida: Boolean,
        foto: String
    ): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("genero", genero)
            put("fechasalida", fechasalida)
            put("sinopsis", sinopsis)
            put("emitida", if (emitida) 1 else 0)
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
    private fun insertarPeliculasDemo() {
        insertPelicula(
            nombre = "Interstellar",
            genero = "Ciencia ficción",
            fechasalida = "2014-11-07",
            sinopsis = "Un grupo de astronautas viaja a través de un agujero de gusano en busca de un nuevo hogar para la humanidad.",
            emitida = true,
            estado = "vista",
            foto = "https://img.europapress.es/fotoweb/fotonoticia_20140507113341_1200.jpg"
        )

        insertPelicula(
            nombre = "Spider-Man: No Way Home",
            genero = "Acción",
            fechasalida = "2021-12-17",
            sinopsis = "Peter Parker enfrenta las consecuencias de que el mundo conozca su identidad y abre las puertas del multiverso.",
            emitida = true,
            estado = "vista",
            foto = "https://static.wikia.nocookie.net/marvelcinematicuniverse/images/d/df/Spider-Man_No_Way_Home_Poster.png/revision/latest?cb=20211201150655&path-prefix=es"
        )


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
                    emitida = cursor.getInt(cursor.getColumnIndexOrThrow("emitida")) == 1,
                    estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                    foto = cursor.getString(cursor.getColumnIndexOrThrow("foto"))
                )
                lista.add(pelicula)
            } while (cursor.moveToNext())
        }

        cursor.close()

        return lista
    }

    fun debugCountPeliculas(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) AS total FROM $TABLE_PELICULA", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndexOrThrow("total"))
        }
        cursor.close()
        return count
    }


}
