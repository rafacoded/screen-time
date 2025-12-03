import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
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

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla pelicula
        val createPelicula = """
            CREATE TABLE $TABLE_PELICULA (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                fechasalida TEXT,
                genero TEXT,
                nombre TEXT,
                sinopsis TEXT,
                emitida BOOLEAN DEFAULT 0
            )
        """
        db.execSQL(createPelicula)

        // Crear tabla resenya
        val createResenya = """
            CREATE TABLE $TABLE_RESENYA (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                descripcion TEXT,
                calificacion INTEGER,
                fecha TEXT,
                id_pelicula INTEGER,
                FOREIGN KEY (id_pelicula) REFERENCES $TABLE_PELICULA(id)
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
        """
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
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RESENYA")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PELICULA")
        onCreate(db)
    }

    fun insertPelicula(nombre: String, genero: String?, fechasalida: String?, sinopsis: String?, emitida: Boolean): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("genero", genero)
            put("fechasalida", fechasalida)
            put("sinopsis", sinopsis)
            put("emitida", if (emitida) 1 else 0)
        }
        return db.insert(TABLE_PELICULA, null, values)
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
        emitida: Boolean
    ): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("genero", genero)
            put("fechasalida", fechasalida)
            put("sinopsis", sinopsis)
            put("emitida", if (emitida) 1 else 0)
        }
        return db.update(TABLE_PELICULA, values, "id = ?", arrayOf(id.toString()))
    }

    fun deletePelicula(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_PELICULA, "id = ?", arrayOf(id.toString()))
    }

    fun insertResenya (
        descripcion: String,
        calificacion: Int,
        fecha: String,
        id_pelicula: Int
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("descripcion", descripcion)
            put("calificacion", calificacion)
            put("fecha", fecha)
            put("id_pelicula", id_pelicula)
        }
        return db.insert(TABLE_RESENYA, null, values)
    }
    fun getAllResenyasfun(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_RESENYA", null)
    }
    fun updateResenya(
        id: Int,
        descripcion: String,
        calificacion: Int,
        fecha: String,
        id_pelicula: Int
    ): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("descripcion",descripcion)
            put("calificacion",calificacion)
            put("fecha",fecha)
            put("id_pelicula",id_pelicula)
        }
        return db.update(TABLE_RESENYA, values, "id = ?", arrayOf(id.toString()))
    }
    fun deleteResenya(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_RESENYA, "id = ?", arrayOf(id.toString()))
    }
    fun insertUsuario(
        nombre: String,
        descripcion: String?,
        contraseña: String?,
        email: String?,
        fotoperfil: String?,
        id_resenya: Int?
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("contraseña", contraseña)
            put("email", email)
            put("fotoperfil", fotoperfil)
            if (id_resenya != null) put("id_resenya", id_resenya)
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
        contraseña: String?,
        email: String?,
        fotoperfil: String?,
        id_resenya: Int?
    ): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("contraseña", contraseña)
            put("email", email)
            put("fotoperfil", fotoperfil)
            if (id_resenya != null) put("id_resenya", id_resenya)
        }
        return db.update(TABLE_USUARIO, values, "id = ?", arrayOf(id.toString()))
    }

    fun deleteUsuario(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_USUARIO, "id = ?", arrayOf(id.toString()))
    }

    fun insertRecordatorio(
        nombre: String,
        descripcion: String?,
        fecha: Date,
        id_pelicula: Int?,
        id_usuario: Int?
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("fecha", fecha.time)
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

}
