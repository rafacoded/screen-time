package com.example.screentime.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.screentime.R
import com.example.screentime.models.Pelicula
import com.example.screentime.utils.esPeliculaEmitida

class PeliculaBuscarAdapter(
    private var lista: List<Pelicula>,
    val onAddClick: (Pelicula) -> Unit,
    val onRecordatorioClick: (Pelicula) -> Unit
) : RecyclerView.Adapter<PeliculaBuscarAdapter.ViewHolder>() {

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val img = v.findViewById<ImageView>(R.id.ivCardImagenBuscar)
        val titulo = v.findViewById<TextView>(R.id.tvCardTituloBuscar)
        val genero = v.findViewById<TextView>(R.id.tvCardGeneroBuscar)
        val btnAdd = v.findViewById<Button>(R.id.btnAdd)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pelicula_buscar, parent, false)
        return ViewHolder(vista)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = lista[position]

        holder.titulo.text = p.nombre
        holder.genero.text = p.genero ?: "-"

        Glide.with(holder.itemView.context)
            .load(p.foto)
            .into(holder.img)

        holder.btnAdd.setOnClickListener { onAddClick(p) }


    }

    override fun getItemCount() = lista.size

    fun updateList(nueva: List<Pelicula>) {
        lista = nueva
        notifyDataSetChanged()
    }
}
