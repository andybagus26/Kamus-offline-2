package com.frostdev.sukamus.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.frostdev.sukamus.R
import com.frostdev.sukamus.model.ModelKamus

/**
 * Adapter sederhana untuk menampilkan daftar ModelKamus (kata + deskripsi)
 * di dalam RecyclerView.
 */
class KamusAdapter(private var items: List<ModelKamus?>) :
    RecyclerView.Adapter<KamusAdapter.KamusViewHolder>() {

    class KamusViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvKata: TextView = view.findViewById(R.id.tv_kata)
        val tvDeskripsi: TextView = view.findViewById(R.id.tv_deskripsi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KamusViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_kamus, parent, false)
        return KamusViewHolder(view)
    }

    override fun onBindViewHolder(holder: KamusViewHolder, position: Int) {
        val kamus = items[position]
        holder.tvKata.text = kamus?.kata ?: ""
        holder.tvDeskripsi.text = kamus?.deskripsi ?: ""
    }

    override fun getItemCount(): Int = items.size

    /**
     * Mengganti seluruh data adapter dengan hasil pencarian/list baru,
     * lalu refresh tampilan RecyclerView.
     */
    fun updateData(newItems: List<ModelKamus?>) {
        items = newItems
        notifyDataSetChanged()
    }
}