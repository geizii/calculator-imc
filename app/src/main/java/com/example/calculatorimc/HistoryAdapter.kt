package com.example.calculatorimc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class HistoryAdapter(private var imcRecords: List<ImcRecord>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
        val weightTextView: TextView = view.findViewById(R.id.weightTextView)
        val heightTextView: TextView = view.findViewById(R.id.heightTextView)
        val imcTextView: TextView = view.findViewById(R.id.imcTextView)
        val categoryTextView: TextView = view.findViewById(R.id.categoryTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = imcRecords[position]

        // Formata os valores para exibição
        val numberFormat = NumberFormat.getNumberInstance(Locale("pt", "BR"))
        numberFormat.maximumFractionDigits = 1

        // Preenche os dados na view
        holder.dateTextView.text = record.date
        holder.weightTextView.text = "${numberFormat.format(record.weight)} kg"
        holder.heightTextView.text = "${numberFormat.format(record.height)} m"
        holder.imcTextView.text = numberFormat.format(record.imc)
        holder.categoryTextView.text = record.category

        // Define a cor da categoria baseada no IMC
        val categoryColor = when {
            record.imc < 18.5 -> "#FF9800" // Laranja para abaixo do peso
            record.imc < 25 -> "#4CAF50" // Verde para peso normal
            record.imc < 30 -> "#FFC107" // Amarelo para sobrepeso
            record.imc < 35 -> "#FF5722" // Laranja escuro para obesidade I
            record.imc < 40 -> "#F44336" // Vermelho para obesidade II
            else -> "#D32F2F" // Vermelho escuro para obesidade III
        }

        holder.categoryTextView.setTextColor(android.graphics.Color.parseColor(categoryColor))
    }

    override fun getItemCount() = imcRecords.size

    fun updateData(newRecords: List<ImcRecord>) {
        imcRecords = newRecords
        notifyDataSetChanged()
    }
}