package com.example.screentime.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.screentime.R
import com.example.screentime.database.DBHelper
import com.example.screentime.models.Recordatorio

class RecordatorioAdapter(
    private val lista: List<Recordatorio>,
    private val onClick: (Recordatorio) -> Unit
) : RecyclerView.Adapter<RecordatorioAdapter.RecordatorioViewHolder>() {

    inner class RecordatorioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPoster = view.findViewById<ImageView>(R.id.ivPosterRecordatorio)
        val tvNombre = view.findViewById<TextView>(R.id.tvNombreRecordatorio)
        val tvFecha = view.findViewById<TextView>(R.id.tvFechaRecordatorio)
        val tvDescripcion = view.findViewById<TextView>(R.id.tvDescripcionRecordatorio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordatorioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recordatorio, parent, false)
        return RecordatorioViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordatorioViewHolder, position: Int) {
        val rec = lista[position]

        holder.tvNombre.text = rec.nombre
        holder.tvFecha.text = rec.fecha
        holder.tvDescripcion.text = rec.descripcion ?: ""

        if (rec.idPelicula != null) {

            val dbHelper = DBHelper(holder.itemView.context)
            val pelicula = dbHelper.getPeliculaById(rec.idPelicula)

            Glide.with(holder.itemView.context)
                .load(pelicula?.foto)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(holder.ivPoster)

        } else {
            holder.ivPoster.setImageResource(R.drawable.placeholder)
        }

        holder.itemView.setOnClickListener {
            onClick(rec)
        }
    }

    override fun getItemCount() = lista.size
}
