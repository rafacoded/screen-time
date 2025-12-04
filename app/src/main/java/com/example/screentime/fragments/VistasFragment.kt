package com.example.screentime.fragments

import DBHelper
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
import com.example.screentime.paginas.PeliculaActivity
import kotlin.jvm.java

class VistasFragment : Fragment() {

    private lateinit var db: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PeliculaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_vistas, container, false)

        db = DBHelper(requireContext())
        recyclerView = view.findViewById(R.id.rvVistas)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val lista = db.getPeliculasVistas()

        adapter = PeliculaAdapter(lista) { pelicula ->
            abrirDetalle(pelicula.id)
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