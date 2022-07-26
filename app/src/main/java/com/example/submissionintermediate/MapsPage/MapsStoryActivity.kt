package com.example.submissionintermediate.MapsPage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submissionintermediate.Data.UserPreference
import com.example.submissionintermediate.Data.ViewModelFactory
import com.example.submissionintermediate.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.submissionintermediate.databinding.ActivityMapsStoryBinding
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.io.IOException
import java.net.URL


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MapsStoryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsStoryBinding
    private lateinit var mapsStoryViewModel : MapsStoryViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setupViewModel()

    }

    private fun setupViewModel(){
        val pref =UserPreference.getInstance(dataStore)
        mapsStoryViewModel = ViewModelProvider(
            this,
            ViewModelFactory(pref, this)
        )[MapsStoryViewModel::class.java]

        mapsStoryViewModel.getUser().observe(this){user ->
            mapsStoryViewModel.setStoriesLocation(token = user.token)
        }
        getStoriesLocation()

    }

    private fun getStoriesLocation(){
        mapsStoryViewModel.getStoriesLocation().observe(this){stories ->

            val storiesLatLon =stories?.map {
                LatLng(
                    (it.lat) as Double,
                    (it.lon) as Double)
            }
            if (storiesLatLon!!.isNotEmpty()){
                storiesLatLon.forEachIndexed{index, latLng ->
                    val url = URL(stories[index].photoUrl)
                    val photo = url.toBitmap()
                    val photoMarker = Bitmap.createScaledBitmap(photo!!, 120, 120, false)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(stories[index].name)
                            .icon(BitmapDescriptorFactory.fromBitmap(photoMarker))
                    )
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(storiesLatLon[1]))
                }
            }
        }
    }

    fun URL.toBitmap(): Bitmap? {
        return try {
            BitmapFactory.decodeStream(openStream())
        } catch (e: IOException) {
            null
        }
    }

}