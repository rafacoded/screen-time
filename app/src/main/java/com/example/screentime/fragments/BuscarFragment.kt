package com.example.screentime.fragments

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.screentime.R
import com.example.screentime.adapters.BuscarPeliculaAdapter
import com.example.screentime.database.DBHelper
import com.example.screentime.models.Pelicula

class BuscarFragment : Fragment(R.layout.fragment_buscar) {

    private lateinit var db: DBHelper
    private lateinit var adapter: BuscarPeliculaAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView

    private var listaCompleta = listOf<Pelicula>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DBHelper(requireContext())

        recyclerView = view.findViewById(R.id.rvBuscar)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        searchView = view.findViewById(R.id.searchView)
        searchView.clearFocus()

        // Cargar todas las películas
        listaCompleta = db.getAllPeliculasList()
        adapter = BuscarPeliculaAdapter(listaCompleta,
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

    private fun mostrarPopupAñadir(pelicula: Pelicula) {
        // Aquí luego haces un popup estilo BottomSheet para elegir:
        // - Pendiente
        // - Vista
    }

    private fun mostrarPopupRecordatorio(pelicula: Pelicula) {
        // Aquí luego abres un popup calendario para seleccionar fecha de recordatorio
    }
}
