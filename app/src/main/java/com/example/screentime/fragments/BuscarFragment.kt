package com.example.screentime.fragments

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.screentime.R
import com.example.screentime.adapters.PeliculaBuscarAdapter
import com.example.screentime.database.DBHelper
import com.example.screentime.models.Pelicula
import com.example.screentime.session.SessionManager
import com.example.screentime.utils.esPeliculaEmitida

class BuscarFragment : Fragment(R.layout.fragment_buscar) {

    private lateinit var db: DBHelper
    private lateinit var adapter: PeliculaBuscarAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var listaCompleta = listOf<Pelicula>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DBHelper(requireContext())

        val userId = SessionManager(requireContext()).getUserId()

        if (userId == -1) {
            Toast.makeText(requireContext(), "Error: usuario no identificado", Toast.LENGTH_SHORT).show()
        }

        recyclerView = view.findViewById(R.id.rvBuscar)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        searchView = view.findViewById(R.id.searchView)
        searchView.clearFocus()

        // Cargar todas las películas
        listaCompleta = db.getAllPeliculasList()
        adapter = PeliculaBuscarAdapter(listaCompleta,
            onAddClick = { pelicula -> mostrarPopupAñadir(pelicula) },
            onRecordatorioClick = { pelicula -> mostrarPopupRecordatorio(pelicula) }
        )
        recyclerView.adapter = adapter

        // Listener del buscador
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(nuevoTexto: String?): Boolean {
                filtrar(nuevoTexto ?: "")
                return true
            }
        })
    }

    private fun filtrar(texto: String) {
        val filtrada = listaCompleta.filter {
            it.nombre.contains(texto, ignoreCase = true) ||
                    (it.genero?.contains(texto, ignoreCase = true) == true)
        }
        adapter.updateList(filtrada)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun mostrarPopupAñadir(pelicula: Pelicula) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Añadir película")
            .setMessage("¿Cómo quieres añadir ${pelicula.nombre}?")
            .setPositiveButton("Vista") { _, _ -> añadirComo("vista", pelicula) }
            .setNegativeButton("Pendiente") { _, _ -> añadirComo("pendiente", pelicula) }
            .setNeutralButton("Cancelar", null)
            .create()

        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun añadirComo(estado: String, pelicula: Pelicula) {

        val userId = SessionManager(requireContext()).getUserId()

        if (db.existePeliculaUsuario(pelicula.id, userId)) {
            Toast.makeText(requireContext(), "Ya está añadida", Toast.LENGTH_SHORT).show()
            return
        }

        if (estado == "vista" && !pelicula.esPeliculaEmitida()) {
            Toast.makeText(requireContext(), "No puedes marcar como vista una película no emitida", Toast.LENGTH_SHORT).show()
            return
        }

        db.insertPeliculaUsuario(
            id_pelicula = pelicula.id,
            id_usuario = userId,
            estado = estado
        )

        Toast.makeText(requireContext(), "Añadida a $estado", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarPopupRecordatorio(pelicula: Pelicula) {
        // Aquí luego abres un popup calendario para seleccionar fecha de recordatorio
    }

}
