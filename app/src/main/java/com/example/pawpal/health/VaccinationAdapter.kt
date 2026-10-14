package com.example.pawpal.health

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pawpal.R

data class VaccinationRecordUi(val id: Int, val title: String, val date: String)

class VaccinationAdapter(
    private val onClick: (Vaccination) -> Unit
) : RecyclerView.Adapter<VaccinationAdapter.Vh>() {

    private val items = mutableListOf<Vaccination>()

    class Vh(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_vaccine_name)
        val tvDate:  TextView = itemView.findViewById(R.id.tv_vaccine_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vaccination_record, parent, false)
        return Vh(v)
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item.title
        holder.tvDate.text  = item.date
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size

    fun submit(list: List<Vaccination>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}
