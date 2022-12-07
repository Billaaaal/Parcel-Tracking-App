package com.example.parcelapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter

class ViewPagerAdapter(val context: Context) : PagerAdapter() {

    var layoutInflater : LayoutInflater? = null

    val imgArray = arrayOf(
        R.drawable.unnamed,
        R.drawable.images2,
        R.drawable.images3,

    )

    val headArray = arrayOf(
        "The all-in-one parcel tracking app",
        "A user-friendly user interface",
        "Over 300+ carriers available",
    )

    val descArray = arrayOf(
        "Track your parcels and track them to know when they will arrive",
        "An app designed to fit perfectly your needs",
        "Including FedEx, Dpd, Dhl and more..."
    )


    override fun getCount(): Int {

        return headArray.size


    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {

        return view == `object` as ConstraintLayout

    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.slider, container, false)
        val img = view.findViewById<ImageView>(R.id.imageView)
        val text_head = view.findViewById<TextView>(R.id.title)
        val text_desc = view.findViewById<TextView>(R.id.description)

        img.setImageResource(imgArray[position])
        text_head.text = headArray[position]
        text_desc.text = descArray[position]








        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }

}
