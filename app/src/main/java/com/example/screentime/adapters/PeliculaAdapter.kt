package com.example.screentime.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.screentime.R
import com.example.screentime.models.Pelicula
import com.google.android.material.chip.Chip

class PeliculaAdapter(

    val lista: List<Pelicula>,
    val onClick: (Pelicula) -> Unit

) : RecyclerView.Adapter<PeliculaAdapter.ViewHolder>() {
    inner class ViewHolder(v : View) : RecyclerView.ViewHolder(v) {

        val img = v.findViewById<ImageView>(R.id.ivCardImagen)
        val titulo = v.findViewById<TextView>(R.id.tvCardTitulo)
        val chipGenero = v.findViewById<Chip>(R.id.chipGeneroCard)
        val chipEstado = v.findViewById<Chip>(R.id.chipEstadoCard)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pelicula, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pelicula = lista[position]

        holder.titulo.text = pelicula.nombre
        holder.chipGenero.text = pelicula.genero ?: "-"

        when (pelicula.estado) {
            "pendiente" -> {
                holder.chipEstado.setChipBackgroundColorResource(R.color.colorSecondary)
                holder.chipEstado.text = R.string.chipEstadoP.toString()
            }
            "vista"     -> {
                holder.chipEstado.setChipBackgroundColorResource(R.color.colorSuccess)
                holder.chipEstado.text = R.string.chipEstadoV.toString()
            }
        }

        Glide.with(holder.itemView.context)
            .load(pelicula.foto)
            .into(holder.img)

        holder.itemView.setOnClickListener {
            onClick(pelicula)
        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}