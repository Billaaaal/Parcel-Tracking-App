package com.example.parcelapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewAdapterHistory(private val list: ArrayList<ItemViewsModelHistory>) :
    RecyclerView.Adapter<RecyclerViewAdapterHistory.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_history, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModelHistory = holder.bindItems(list[position])
        val  ItemsViewModelHistory_ = list[position]



        holder.statusTextView.text = ItemsViewModelHistory_.statusText

        holder.dateTextView.text = ItemsViewModelHistory_.dateText

        holder.statusImageView.setImageResource(ItemsViewModelHistory_.statusImage)

        //Log.i("PSG", ItemsViewModel.icon)

        // when(ItemsViewModelHistory.icon){
        //"01"-> holder.statusImageView.setImageResource(R.drawable.gray_circle)
        // "02"-> holder.statusImageView.setImageResource(R.drawable.red_circle)

        // else -> {
        //holder.imageView.setImageResource(R.drawable.gray_circle)

        // }


        //}




    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return list.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var statusTextView = itemView.findViewById<TextView>(R.id.statusTextView)
        var dateTextView = itemView.findViewById<TextView>(R.id.dateTextView)
        var statusImageView = itemView.findViewById<ImageView>(R.id.statusImageView)

        fun bindItems(item: ItemViewsModelHistory) {


        }
    }

}