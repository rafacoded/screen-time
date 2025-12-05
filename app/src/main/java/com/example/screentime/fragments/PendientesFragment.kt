package com.example.screentime.fragments

import com.example.screentime.database.DBHelper
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.screentime.R
import com.example.screentime.adapters.PeliculaAdapter
import com.example.screentime.paginas.PeliculaActivity
import kotlin.jvm.java

class PendientesFragment : Fragment() {

    private lateinit var db: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PeliculaAdapter
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_pendientes, container, false)

        userId = arguments?.getInt("userId") ?: -1

        db = DBHelper(requireContext())
        recyclerView = view.findViewById(R.id.rvPendientes)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val lista = db.getPeliculasUsuarioPorEstado(userId, "pendiente")
        adapter = PeliculaAdapter(lista) { peliculaConEstado ->
            abrirDetalle(peliculaConEstado.pelicula.id)
        }

        recyclerView.adapter = adapter

        return view
    }

    private fun abrirDetalle(id: Int) {
        val intent = Intent(requireContext(), PeliculaActivity::class.java)
        intent.putExtra("peliculaId", id)
        startActivity(intent)
    }
}