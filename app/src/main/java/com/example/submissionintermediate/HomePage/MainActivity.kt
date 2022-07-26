package com.example.submissionintermediate.HomePage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionintermediate.Data.User
import com.example.submissionintermediate.Data.UserPreference
import com.example.submissionintermediate.Data.ViewModelFactory
import com.example.submissionintermediate.LoadingStateAdapter
import com.example.submissionintermediate.Login.LoginActivity
import com.example.submissionintermediate.MapsPage.MapsStoryActivity
import com.example.submissionintermediate.NewStory.NewStoryActivity
import com.example.submissionintermediate.R
import com.example.submissionintermediate.StoryRepository
import com.example.submissionintermediate.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.internal.notify

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private  lateinit var mainViewModel : MainViewModel
    private lateinit var adapter: StoryAdapter
    private lateinit var pref : UserPreference
    private lateinit var fusedLocationClient : FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = UserPreference.getInstance(dataStore)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()

        setupViewModel()

    }

    override fun onStart() {
        super.onStart()
        setupViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.logout -> {
                mainViewModel.logOut()
                true
            }
            R.id.addNewStory -> {
                val intent = Intent(this, NewStoryActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.to_map -> {
                val intent = Intent(this, MapsStoryActivity::class.java)
                startActivity(intent)
                true
            }
            else -> true
        }
    }

    private fun setupViewModel(){

        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this, {user ->
            if (!user.isLogin) {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else{
                mainViewModel.logIn()
                setUserStory(user.token)
            }
        })
    }

    private fun setUserStory(token:String){
        adapter = StoryAdapter()
        binding.rvPhotos.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
        binding.rvPhotos.layoutManager = LinearLayoutManager(this)
        mainViewModel.getStoryPage(token).observe(this, {
            adapter.submitData(lifecycle, it)
            showLoading(false)
        })
    }


    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permission->
        when{
            permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: false ->{
                getLastLocation()
            }
            permission[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false ->{
                getLastLocation()
            }
        }
    }

    private fun checkPermission(permission:String):Boolean{
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLastLocation(){
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)&&checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)){
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it != null){
                    val lat = it.latitude
                    val lng = it.longitude
                } else{
                    Toast.makeText(this, "location not found", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    private fun showLoading(state: Boolean) {
        if(state) {
            binding.pbMain.visibility = View.VISIBLE
        } else {
            binding.pbMain.visibility = View.GONE
        }
    }

}