package com.example.screentime.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.screentime.R
import com.example.screentime.models.Pelicula
import com.example.screentime.models.PeliculaConEstado
import com.google.android.material.chip.Chip

class PeliculaAdapter(

    var lista: List<PeliculaConEstado>,
    val onClick: (PeliculaConEstado) -> Unit,

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
        val item = lista[position]
        val pelicula = item.pelicula

        holder.titulo.text = pelicula.nombre
        holder.chipGenero.text = pelicula.genero ?: "-"
        holder.chipEstado.text = item.estado

        when (item.estado) {
            "pendiente" -> {
                holder.chipEstado.setChipBackgroundColorResource(R.color.colorSecondary)
            }
            "vista" -> {
                holder.chipEstado.setChipBackgroundColorResource(R.color.colorSuccess)
            }
            else -> {
                holder.chipEstado.visibility = View.GONE
            }
        }

        Glide.with(holder.itemView.context)
            .load(pelicula.foto)
            .into(holder.img)

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int {
        return lista.size
    }

}