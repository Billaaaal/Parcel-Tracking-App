package com.example.parcelapp

import android.icu.text.DateFormat.getDateInstance
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.sql.Timestamp
import java.text.DateFormat
import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class TrackingDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_details)
        supportActionBar?.hide()

        var textView = findViewById<TextView>(R.id.textView)

        var back_button = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.back_button)

        var recycler_view = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view)

        var carrierImageView  = findViewById<ImageView>(R.id.carrierImageView)

        var daysCount = findViewById<TextView>(R.id.daysCount)

        var textViewHistory = findViewById<TextView>(R.id.textViewHistory)


        var parcelNumberTextView = findViewById<TextView>(R.id.parcelNumberTextView)

        var textViewFrom = findViewById<TextView>(R.id.textViewFrom)

        var textViewTo = findViewById<TextView>(R.id.textViewTo)

        var textViewStatus = findViewById<TextView>(R.id.textViewStatus)

        var textViewWeight = findViewById<TextView>(R.id.textViewWeight)






        recycler_view.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // ArrayList of class ItemsViewModel
        var data = ArrayList<ItemViewsModelHistory>()

        // This loop will create 20 Views containing
        // the image with the count of view
        // for (i in 1..5) {
        //      data.add(ItemsViewModel(R.layout.card_view_design, temp1+"°C", date1.toString()))
        // }











        // This will pass the ArrayList to our Adapter
        //val adapter = RecyclerViewAdapterHistory(data)

        // Setting the Adapter with the recyclerview
        //recycler_view.adapter = adapter

        back_button.setOnClickListener {
            finish()
        }

        var trackingId = getIntent().getStringExtra("trackingId")

        //textView.text = trackingId

        val client = OkHttpClient()

        val mediaType = "application/json".toMediaTypeOrNull()

        var body = RequestBody.create(
            mediaType,
            ""

        )

        val mySearchUrl: HttpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("api.aftership.com")
            .addPathSegment("v4")
            .addPathSegment("trackings")
            .addPathSegment("${trackingId}")
            .build()

        val request = Request.Builder()
            //.url("https://listen-api.listennotes.com/api/v2/podcasts/" + id_ + "?sort=recent_first")
            .url(mySearchUrl)
            .method("GET", null)
            .addHeader("as-api-key", "asat_bb80468439ef4ab1af5d646032b8cd02")
            .addHeader("Content-Type", "application/json")
            //.header("X-ListenAPI-Key", "c7a88e0f1a17445bb4f14b4212fa161f")
            //.header("Accept", "application/json")
            .build()

        var response__ = ""

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response_: Response) {


                response_.use {

                    var response_ = response_.body!!.string()

                    val gson_ = GsonBuilder().setPrettyPrinting().create()
                    var prettyJson_ = gson_.toJson(
                        JsonParser.parseString(
                            response_ // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                        )
                    )


                    var obj_ = JSONObject(prettyJson_)

                    var code = obj_.getJSONObject("meta").getString("code")



                    //Log.i("Responses", obj_.toString())

                    if (code=="4003"||code=="4000"||code=="201"||code=="200"){
                        var trackingArray = obj_.getJSONObject("data").getJSONObject("tracking")

                        var trackingNumber = trackingArray.getString("tracking_number")

                        runOnUiThread {
                            parcelNumberTextView.text = "#${trackingNumber}"

                        }



                        var carrier = trackingArray.getString("slug")

                        runOnUiThread{
                            GlideToVectorYou
                                .init()
                                .with(this@TrackingDetailsActivity)
                                .setPlaceHolder(R.drawable.parcel, R.drawable.parcel)
                                .load(Uri.parse("https://assets.aftership.com/couriers/svg/${carrier}.svg"), carrierImageView)

                        }



                        var parcelStatus = trackingArray.getString("subtag_message")

                        runOnUiThread {
                            textViewStatus.text = parcelStatus
                        }





                        try {
                            var parcelWeight = trackingArray.getString("shipment_weight")
                            var parcelUnit = trackingArray.getString("shipment_weight_unit")
                            runOnUiThread {
                                textViewWeight.text = parcelWeight + " " + parcelUnit

                            }

                        } catch (e : Exception) {
                            runOnUiThread {
                                textViewWeight.text = "--"

                            }

                        }

                        // to get days since package was created, get current time in timestamp and substract the date of the first checkpoint(0) from the current time stam
                        //and then convert it to days by dividing it by 86400 Seconds (which is equal to one day)












                        var checkpoints = trackingArray.getJSONArray("checkpoints")

                        Log.i("ARRAYY", "${checkpoints.length()}")

                        if("${checkpoints.length()}" == "0"){
                            runOnUiThread {
                                textViewFrom.text = "--"
                                textViewTo.text = "--"
                                textViewHistory.text = "No history"
                            }

                        }else{
                            if(trackingArray.getString("tag")=="Delivered"){
                                var firstCheckPointDate = checkpoints.getJSONObject(0).getString("checkpoint_time")
                                val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                // you can change format of date
                                // you can change format of date
                                val date: Date = formatter.parse(firstCheckPointDate)
                                val firstDate = date.getTime()


                                var lastCheckPointDate = checkpoints.getJSONObject(checkpoints.length()-1).getString("checkpoint_time")
                                val formatter_: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                // you can change format of date
                                // you can change format of date
                                val date_: Date = formatter.parse(lastCheckPointDate)
                                val lastDate = date_.getTime()



                                runOnUiThread {
                                    daysCount.text = "${(((lastDate-firstDate)/1000)/86400).toInt()} days"

                                }


                            }else{
                                var firstCheckPointDate = checkpoints.getJSONObject(0).getString("checkpoint_time")

                                val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                // you can change format of date
                                // you can change format of date
                                val date: Date = formatter.parse(firstCheckPointDate)
                                val timeStampDate = date.getTime()


                                var todayDate = System.currentTimeMillis()



                                runOnUiThread {
                                    daysCount.text = "${(((todayDate-timeStampDate)/1000)/86400).toInt()} days"

                                }


                            }










                            for (i in 0 until checkpoints.length()) {
                                Log.i("DIDREACHED", i.toString())

                                var error = false
                                var location = ""
                                var part1 = ""
                                var part2 = ""
                                var checkpoint = checkpoints.getJSONObject(i)
                                try {
                                    var test = checkpoint.getString("country_name")
                                    var error = false

                                    //break


                                } catch (e : Exception) {
                                    error = true
                                }
                                if (error){

                                }else{
                                    var country_name = checkpoint.getString("country_name")
                                    var checkpoint_location = checkpoint.getString("location")

                                    if (checkpoint_location.contains(" (", ignoreCase = true) && checkpoint_location.contains("), ", ignoreCase = true)){
                                        part1 = checkpoint_location.split(" (")[0]
                                        part2 = checkpoint_location.split("), ")[1]
                                        location = "$part1, $part2"
                                        //Log.i("DIDREACHED", "YESS")
                                    }else{
                                        location = checkpoint_location
                                    }
                                    runOnUiThread {
                                        textViewFrom.text = location
                                    }
                                    break
                                }
                                //Log.i("CHECKPOINTSOOO", "${checkpoint.getString("
                                // now do something with the Object
                            }

                            for (i in checkpoints.length()-1 downTo 0) {
                                Log.i("DIDREACHED", i.toString())

                                var error = false
                                var location = ""
                                var part1 = ""
                                var part2 = ""
                                var checkpoint = checkpoints.getJSONObject(i)
                                try {
                                    var test = checkpoint.getString("country_name")
                                    var error = false

                                    //break


                                } catch (e : Exception) {
                                    error = true
                                }
                                if (error){

                                }else{
                                    var country_name = checkpoint.getString("country_name")
                                    var checkpoint_location = checkpoint.getString("location")

                                    if (checkpoint_location.contains(" (", ignoreCase = true) && checkpoint_location.contains("), ", ignoreCase = true)){
                                        part1 = checkpoint_location.split(" (")[0]
                                        part2 = checkpoint_location.split("), ")[1]
                                        location = "$part1, $part2"
                                        //Log.i("DIDREACHED", "YESS")
                                    }else{
                                        location = checkpoint_location
                                    }
                                    runOnUiThread {
                                        textViewTo.text = location
                                    }
                                    break
                                }
                                //Log.i("CHECKPOINTSOOO", "${checkpoint.getString("
                                // now do something with the Object
                            }


                            for (i in checkpoints.length()-1 downTo 0) {
                                var checkpoint = checkpoints.getJSONObject(i)
                                //Log.i("CHECKPOINTSOOO", "${checkpoint.getString("checkpoint_time").replace("T", " ")}" +
                                //        "${checkpoint.getString("message")}")

                                var message = checkpoint.getString("message").replace(".", "")

                                var date_ = checkpoint.getString("checkpoint_time")

                                val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                // you can change format of date
                                // you can change format of date
                                val date: Date = formatter.parse(date_)
                                val timeStampDate = date.getTime()

                                var formatter_output =
                                    DateTimeFormatter.ofPattern("dd MMMM',' kk:mm", Locale.ENGLISH)



                                var he  = Instant.ofEpochSecond(timeStampDate.toString().take(10).toLong())
                                    .atZone(ZoneId.of("GMT+2"))
                                    .format(formatter_output)





                                Log.i("STAMP", he.toString())

                                var part1: String
                                var part2: String
                                var location: String = ""




                                var checkpoint_location = checkpoint.getString("location")

                                if (checkpoint_location.contains(" (", ignoreCase = true) && checkpoint_location.contains("), ", ignoreCase = true)){
                                    part1 = checkpoint_location.split(" (")[0]
                                    part2 = checkpoint_location.split("), ")[1]

                                    location = "$part1, $part2"

                                }else{
                                    location = checkpoint_location
                                }






                                var image : Int
                                if(i == checkpoints.length()-1){
                                    image = R.drawable.red_circle

                                }else{
                                    image = R.drawable.gray_circle
                                }

                                data.add(ItemViewsModelHistory(R.layout.card_view_design, message, "${he} • ${location}", image))
                                // now do something with the Object
                            }

                            runOnUiThread {
                                val adapter = RecyclerViewAdapterHistory(data)

                                // Setting the Adapter with the recyclerview
                                recycler_view.adapter = adapter
                            }

                        }








                    }else{
                        //ERROR
                        runOnUiThread {
                            Toast.makeText(this@TrackingDetailsActivity, "Error", Toast.LENGTH_SHORT).show()

                        }

                    }

                    //Log.i("DETAILS", prettyJson_.toString())









                    //Log.i("Responses", obj_.toString())
                }
            }

        })

    }


}