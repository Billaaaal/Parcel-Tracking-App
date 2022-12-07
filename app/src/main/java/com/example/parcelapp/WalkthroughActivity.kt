package com.example.parcelapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import java.security.AccessController.getContext


class WalkthroughActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walkthrough)
        supportActionBar?.hide()

        val dotsIndicator = findViewById<DotsIndicator>(R.id.dots_indicator)


        var viewPager = findViewById<androidx.viewpager.widget.ViewPager>(R.id.ViewPager)
        var viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter



        var next_button  = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.next_button)

        next_button.setOnClickListener(){
            //mViewPager.setCurrentItem(getItem(+1), true)
            viewPager.setCurrentItem(viewPager.currentItem+1, true);

        }

        var skip_button = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.skip_button)
        skip_button.visibility = View.VISIBLE


        skip_button.setOnClickListener(){
            //Toast.makeText(view.getContext(), "HI", Toast.LENGTH_SHORT).show()
            val readMore = Intent(getBaseContext(), MainActivity::class.java)
            var mPrefs = getSharedPreferences("FirstTime", 0)
            var editor = mPrefs.edit()
            editor.putBoolean("FirstTime", false)
            editor.apply()
            startActivity(readMore)
            finish()


        }


        dotsIndicator.attachTo(viewPager)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

                if (position==2){
                    skip_button.visibility = View.INVISIBLE
                    next_button.setOnClickListener(){
                        val readMore = Intent(getBaseContext(), MainActivity::class.java)
                        var mPrefs = getSharedPreferences("FirstTime", 0)
                        var editor = mPrefs.edit()
                        editor.putBoolean("FirstTime", false)
                        editor.apply()
                        startActivity(readMore)
                        finish()


                    }

                }else{
                    skip_button.visibility = View.VISIBLE

                    next_button.setOnClickListener(){
                        viewPager.setCurrentItem(viewPager.currentItem+1, true);

                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })


    }
}