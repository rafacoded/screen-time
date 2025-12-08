package com.example.screentime.fragments

import com.example.screentime.database.DBHelper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.screentime.R
import com.example.screentime.adapters.PeliculaAdapter
import com.example.screentime.paginas.PeliculaActivity
import com.example.screentime.session.SessionManager
import kotlin.jvm.java

class PendientesFragment : Fragment() {

    private lateinit var db: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PeliculaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_pendientes, container, false)

        val userId = SessionManager(requireContext()).getUserId()

        if (userId == -1) {
            Toast.makeText(requireContext(), "Error: usuario no identificado", Toast.LENGTH_SHORT).show()
        }

        db = DBHelper(requireContext())

        recyclerView = view.findViewById(R.id.rvPendientes)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val lista = db.getPeliculasUsuarioPorEstado(userId, "pendiente")

        adapter = PeliculaAdapter(lista) {
            abrirDetalle(it.pelicula.id)
        }

        recyclerView.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        refrescarLista()
    }

    private fun refrescarLista() {
        val userId = SessionManager(requireContext()).getUserId()
        val nuevaLista = db.getPeliculasUsuarioPorEstado(userId, "pendiente")
        adapter.lista = nuevaLista
        adapter.notifyDataSetChanged()
    }

    private fun abrirDetalle(id: Int) {
        val intent = Intent(requireContext(), PeliculaActivity::class.java)
        intent.putExtra("peliculaId", id)
        startActivity(intent)
    }
}

