package com.example.pawpal.health

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpal.R

class AllergyAdapter(
    private val onClick: (Allergy) -> Unit
) : RecyclerView.Adapter<AllergyAdapter.Vh>() {

    private val items = mutableListOf<Allergy>()

    class Vh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_allergy_name)
        // В item_allergy_record можно не использовать иконки здесь,
        // редактирование/удаление делаем через диалог по клику на item.
        val ivEdit: ImageView? = itemView.findViewById(R.id.ivEdit)
        val ivDelete: ImageView? = itemView.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_allergy_record, parent, false)
        return Vh(v)
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item.title
        holder.itemView.setOnClickListener { onClick(item) }
        holder.ivEdit?.setOnClickListener { onClick(item) }
        holder.ivDelete?.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size

    fun submit(list: List<Allergy>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}
