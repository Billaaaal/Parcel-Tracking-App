package com.example.parcelapp

import android.Manifest
import android.content.Intent
import android.content.res.Resources.Theme
import android.location.*
import android.location.Address
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.internal.toHexString
import org.json.JSONObject
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val typedValue = TypedValue()
        val theme: Theme = this.getTheme()
        theme.resolveAttribute(androidx.constraintlayout.widget.R.attr.colorControlHighlight, typedValue, true)
        @ColorInt val color = ContextCompat.getColor(this, typedValue.resourceId)

        Log.i("MAIIIZZN", "#" + Integer.toHexString(color).substring(2))

        var mPrefs = getSharedPreferences("FirstTime", 0)
        var FirstTime = mPrefs.getBoolean("FirstTime", true)

        var editor = mPrefs.edit()



        var lPrefs = getSharedPreferences("location", 0)
        var current_location = lPrefs.getString("location", "--")


        var zPrefs = getSharedPreferences("idsList", 0)
        //var jsonText = zPrefs.getString("idsList", "")

        if (FirstTime == true) {
            //Toast.makeText(applicationContext, "Welcome !", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, WalkthroughActivity::class.java).apply {
            }
            startActivity(intent)
            finish();

        } else {
            //Toast.makeText(applicationContext, "Welcome back !", Toast.LENGTH_SHORT).show()

        }




        val gson = Gson()
        val jsonText = zPrefs.getString("idsList", "")

        val recyclerview = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view)


        recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        var data = ArrayList<itemViewModel>()

        val adapter = recyclerViewAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        var recentlyTrackedTextView = findViewById<TextView>(R.id.recentlyTracked)







       // Log.i("GOO", jsonText.toString())

        if(jsonText!=""){
            val text = gson.fromJson(
                jsonText,
                Array<String>::class.java
            )




            for(element in text){
                Log.i("HIII", element)

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
                    .addPathSegment("${element}")
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
                                    //parcelNumberTextView.text = "#${trackingNumber}"

                                }



                                var carrier = trackingArray.getString("slug")








                                var parcelStatus = trackingArray.getString("subtag_message")


                                runOnUiThread {
                                    //textViewStatus.text = parcelStatus
                                }





                                try {
                                    var parcelWeight = trackingArray.getString("shipment_weight")
                                    var parcelUnit = trackingArray.getString("shipment_weight_unit")
                                    runOnUiThread {
                                        //textViewWeight.text = parcelWeight + " " + parcelUnit

                                    }

                                } catch (e : Exception) {
                                    runOnUiThread {
                                        //textViewWeight.text = "--"

                                    }

                                }

                                // to get days since package was created, get current time in timestamp and substract the date of the first checkpoint(0) from the current time stam
                                //and then convert it to days by dividing it by 86400 Seconds (which is equal to one day)












                                var checkpoints = trackingArray.getJSONArray("checkpoints")

                                Log.i("ARRAYY", "${checkpoints.length()}")

                                if("${checkpoints.length()}" == "0"){
                                    runOnUiThread {
                                        //textViewFrom.text = "--"
                                        //textViewTo.text = "--"
                                        //textViewHistory.text = "No history"
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
                                            //daysCount.text = "${(((lastDate-firstDate)/1000)/86400).toInt()} days"

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
                                            //daysCount.text = "${(((todayDate-timeStampDate)/1000)/86400).toInt()} days"

                                        }


                                    }
                                    var from = ""
                                    var to = ""
                                    var fromCountry =""
                                    var toCountry =""












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

                                                //textViewFrom.text = location

                                                from = part1

                                                fromCountry = part2



                                            break
                                        }
                                        //Log.i("CHECKPOINTSOOO", "${checkpoint.getString("
                                        // now do something with the Object
                                    }

                                    for (i in checkpoints.length()-1 downTo 0) {
                                        Log.i("DIDREACHED", i.toString())

                                        var error = false
                                        var location = ""
                                        var part1_ = ""
                                        var part2_ = ""
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
                                                part1_ = checkpoint_location.split(" (")[0]
                                                part2_ = checkpoint_location.split("), ")[1]
                                                location = "$part1_, $part2_"
                                                //Log.i("DIDREACHED", "YESS")
                                            }else{
                                                location = checkpoint_location
                                            }

                                            //textViewTo.text = location
                                            to = part1_
                                            toCountry = part2_

                                            break
                                        }
                                        //Log.i("CHECKPOINTSOOO", "${checkpoint.getString("
                                        // now do something with the Object
                                    }






                                    data.add(itemViewModel(R.layout.card_view_design, element, trackingNumber, from, to, fromCountry, toCountry, carrier, parcelStatus))
                                    runOnUiThread {
                                        adapter.notifyDataSetChanged()

                                    }







                                }








                            }else{
                                //ERROR
                                runOnUiThread {
                                    //Toast.makeText(this@TrackingDetailsActivity, "Error", Toast.LENGTH_SHORT).show()

                                }

                            }

                            //Log.i("DETAILS", prettyJson_.toString())









                            //Log.i("Responses", obj_.toString())
                        }
                    }

                })
                Log.i("GOO", element.toString())
            }

        }else{
            recentlyTrackedTextView.text = "No recently tracked parcels"
        }













        //var zeditor =zPrefs.edit()

        //var leditor = lPrefs.edit()

        //leditor.putString("location", "Miami")
        //leditor.apply()






















        var location_textview =  findViewById<TextView>(R.id.location_textview)

        location_textview.text = lPrefs.getString("location" ,"--")


        var track_button =
            findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.track_button)
        var tracking_input_text =
            findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.tracking_input_text)




        // ArrayList of class ItemsViewModel


        // This loop will create 20 Views containing
        // the image with the count of view
        // for (i in 1..5) {
        //      data.add(ItemsViewModel(R.layout.card_view_design, temp1+"°C", date1.toString()))
        // }












        track_button.setOnClickListener() {

            try {
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
                tracking_input_text.clearFocus();
            } catch (e: Exception) {
                // TODO: handle exception
            }
            var tracking_number = tracking_input_text.text.toString()
            runOnUiThread {
                //Toast.makeText(
                //    applicationContext, "Tracking your parcel n°${
                //        tracking_number
                //    }... ", Toast.LENGTH_SHORT
                //).show()

            }


            val client = OkHttpClient()

            val mediaType = "application/json".toMediaTypeOrNull()

            var body = RequestBody.create(
                mediaType,
                "{\n    \"tracking\": {\n        \"tracking_number\": \"${tracking_number}\"\n    }\n}"
            )

            val request = Request.Builder()
                //.url("https://listen-api.listennotes.com/api/v2/podcasts/" + id_ + "?sort=recent_first")
                .url("https://api.aftership.com/v4/trackings")
                .method("POST", body)
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



                        Log.i("Responses", obj_.toString())

                        if (code=="4003"||code=="4000"||code=="201"||code=="200"){
                            var package_id = obj_.getJSONObject("data").getJSONObject("tracking").getString("id")




                            //var currentTasks = ArrayList<String>()
                            //currentTasks.add(package_id)

                            // save the task list to preference

                            //var zPrefs = getSharedPreferences("idsList", 0)



                            var textList: MutableList<String> = mutableListOf()
                            //var jsonText = zPrefs.getString("idsList", "")




                            var gson = Gson()
                            var jsonText = zPrefs.getString("idsList", "")








                            val text = gson.fromJson(
                                jsonText,
                                Array<String>::class.java
                            )

                            if(jsonText==""){
                                textList.add(package_id)

                            }else{
                                if (text.contains(package_id)){
                                    runOnUiThread {

                                        Toast.makeText(applicationContext, "Existe déjà", Toast.
                                        LENGTH_SHORT).show()

                                    }
                                }else{
                                    textList.add(package_id)

                                }
                                for(element in text){
                                    textList.add(element)

                                    //Toast.makeText(applicationContext, element.toString(), Toast.LENGTH_SHORT).show()
                                }

                            }










                            // save the task list to preference






                            //var textList: MutableList<String> = mutableListOf()
                            //textList.add(package_id)
                            var jsonTextz = gson.toJson(textList)
                            var zeditor = zPrefs.edit()
                            zeditor.putString("idsList", jsonTextz)
                            zeditor.apply()


                            //lancer activité avec les détails de package_id


                            var intent_ = Intent(applicationContext, TrackingDetailsActivity::class.java).apply {
                            }
                            intent_.putExtra("trackingId", package_id);
                            startActivity(intent_)
                            //finish();








                        }else{
                            runOnUiThread {
                                Toast.makeText(applicationContext, "Your tracking number is invalid", Toast.LENGTH_SHORT).show()
                                                 ////////

                            }

                        }



                            



                        //val list = mutableListOf<JSONObject>()

                        //var publisher = obj_.getString("publisher")



                        //for (episode_ in 0 until episodes__.length()) {

                        //    //var main_episode_id = obj_.getJSONArray("results").getJSONObject(episode_)

                        //    var episode = obj_.getJSONArray("episodes").getJSONObject(episode_)

                        //    list.add(episode)


                        //}

                    }
                }
            })
        }


        ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),0x0)





        //var locationManager: LocationManager? = null
        // Create persistent LocationManager reference

        var locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        //val mLocationProvider: FusedLocationProviderClient =
        //     LocationServices.getFusedLocationProviderClient(this)
        //if (ActivityCompat.checkSelfPermission(
        //       this,
        //       Manifest.permission.ACCESS_FINE_LOCATION
        //   ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        //       this,
        //       Manifest.permission.ACCESS_COARSE_LOCATION
        //   ) != PackageManager.PERMISSION_GRANTED
        //) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        //   return
        // }
        //mLocationProvider.getLastLocation().addOnSuccessListener(this) { location ->
        //    if(location != null){
        //        var geocoder = Geocoder(applicationContext, Locale.getDefault())
        //        val addresses: List<Address> =
        //            geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
        //        val cityName: String = addresses.get(0).locality.toString()
        //        Log.i("Hello", "OLD"+ cityName)
//
        //    }

        // }.addOnFailureListener { e -> }

        Thread {
            Thread.sleep(2000)
            runOnUiThread {
                var locationListener: LocationListener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        var thetext = ("" + location.longitude + ":" + location.latitude)
                        var geocoder = Geocoder(applicationContext, Locale.getDefault())
                        val addresses: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)!!
                        val cityName: String = addresses.get(0).locality.toString()
                        //Log.i("Hello", "NEW"+ cityName)

                        location_textview.text = cityName

                        var leditor = lPrefs.edit()
                        leditor.putString("location", cityName)
                        leditor.apply()

                        locationManager?.removeUpdates(this);
                        locationManager = null;








                    }



                }

                try {
                    // Request location updates
                    locationManager?.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        0L,
                        0f,
                        locationListener
                    )
                } catch (ex: SecurityException) {
                    Log.i("myTag", "Security Exception, no location available")
                }

            }
            // Your code
        }.start()











    }

}

//define the listener
