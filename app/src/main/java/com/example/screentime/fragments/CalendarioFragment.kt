package com.example.screentime.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.screentime.R
import com.example.screentime.adapters.RecordatorioAdapter
import com.example.screentime.database.DBHelper
import com.example.screentime.paginas.PeliculaActivity
import com.example.screentime.session.SessionManager

class CalendarioFragment : Fragment() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: RecordatorioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendario, container, false)

        dbHelper = DBHelper(requireContext())
        recycler = view.findViewById(R.id.rvRecordatorios)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        cargarRecordatorios()

        return view
    }

    override fun onResume() {
        super.onResume()
        cargarRecordatorios()
    }

    private fun cargarRecordatorios() {
        val idUsuario = SessionManager(requireContext()).getUserId()

        val lista = dbHelper.getRecordatoriosByUsuario(idUsuario)

        adapter = RecordatorioAdapter(lista) { recordatorio ->

            if (recordatorio.idPelicula != null) {
                val intent = Intent(requireContext(), PeliculaActivity::class.java)
                intent.putExtra("peliculaId", recordatorio.idPelicula)
                startActivity(intent)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Recordatorio sin película asociada",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        recycler.adapter = adapter


        recycler.adapter = adapter
    }

}
