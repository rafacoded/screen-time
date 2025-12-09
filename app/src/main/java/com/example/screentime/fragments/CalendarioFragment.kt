package com.example.screentime.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
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
import com.example.screentime.models.Recordatorio
import com.example.screentime.paginas.PeliculaActivity
import com.example.screentime.session.SessionManager
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class CalendarioFragment : Fragment() {

    lateinit var calendarView: MaterialCalendarView
    private lateinit var dbHelper: DBHelper
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: RecordatorioAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendario, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        recycler = view.findViewById(R.id.rvRecordatorios)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        dbHelper = DBHelper(requireContext())

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

        adapter = RecordatorioAdapter(lista) { rec ->
            if (rec.idPelicula != null) {
                val intent = Intent(requireContext(), PeliculaActivity::class.java)
                intent.putExtra("peliculaId", rec.idPelicula)
                startActivity(intent)
            }
        }
        recycler.adapter = adapter

        resaltarFechasEnCalendario(lista)

        calendarView.setOnDateChangedListener { widget, date, selected ->
            val fechaSeleccionada = "${date.year}-${twoDigits(date.month + 1)}-${twoDigits(date.day)}"

            val delDia = lista.filter { it.fecha == fechaSeleccionada }

            adapter = RecordatorioAdapter(delDia) { rec ->
                if (rec.idPelicula != null) {
                    val intent = Intent(requireContext(), PeliculaActivity::class.java)
                    intent.putExtra("peliculaId", rec.idPelicula)
                    startActivity(intent)
                }
            }
            recycler.adapter = adapter
        }
    }

    private fun resaltarFechasEnCalendario(lista: List<Recordatorio>) {

        val fechas = lista.map {
            val partes = it.fecha.split("-")
            val year = partes[0].toInt()
            val month = partes[1].toInt() - 1
            val day = partes[2].toInt()
            CalendarDay.from(year, month, day)
        }

        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                return fechas.contains(day)
            }

            override fun decorate(view: DayViewFacade?) {
                view?.addSpan(ForegroundColorSpan(Color.RED))
                view?.addSpan(DotSpan(10f, Color.RED))
            }
        })
    }

    private fun twoDigits(n: Int) = if (n < 10) "0$n" else "$n"



}
