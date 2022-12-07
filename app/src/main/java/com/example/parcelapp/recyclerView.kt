package com.example.parcelapp

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.squareup.picasso.Picasso
import kotlinx.coroutines.withContext


class recyclerViewAdapter(private val list: ArrayList<itemViewModel>) :
    RecyclerView.Adapter<recyclerViewAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(list[position])

        val  itemViewModel = list[position]

        GlideToVectorYou
            .init()
            .with(holder.itemView.context)
            .setPlaceHolder(R.drawable.parcel, R.drawable.parcel)
            .load(Uri.parse("https://assets.aftership.com/couriers/svg/${itemViewModel.carrier}.svg"), holder.carrierImageView)







        holder.trackingNumberTextView.text = itemViewModel.trackingNumber

        //Picasso.get().load(url).into(holder.carrierImageView)

        holder.StatusTextview.text = itemViewModel.status

        holder.city1TextView.text = itemViewModel.from
        holder.country1TextView.text = itemViewModel.fromCountry

        holder.city2TextView.text = itemViewModel.to
        holder.country2TextView.text = itemViewModel.toCountry




        holder.itemView.setOnClickListener(View.OnClickListener {
            var intent_ = Intent(holder.itemView.getContext(), TrackingDetailsActivity::class.java).apply {
            }
            intent_.putExtra("trackingId", itemViewModel.trackingId);
            holder.itemView.getContext().startActivity(intent_)

            //OUVRIR ACTIVITE : TRACKINGDETAILSACTIVITY
        })


    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return list.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var trackingNumberTextView = itemView.findViewById<TextView>(R.id.trackingNumberTextView)

        var carrierImageView = itemView.findViewById<ImageView>(R.id.carrierImageView)


        var StatusTextview = itemView.findViewById<TextView>(R.id.StatusTextview)

        var city1TextView = itemView.findViewById<TextView>(R.id.city1TextView)
        var country1TextView = itemView.findViewById<TextView>(R.id.country1TextView)

        var city2TextView = itemView.findViewById<TextView>(R.id.city2TextView)
        var country2TextView = itemView.findViewById<TextView>(R.id.country2TextView)



        fun bindItems(item: itemViewModel) {


        }
    }

}