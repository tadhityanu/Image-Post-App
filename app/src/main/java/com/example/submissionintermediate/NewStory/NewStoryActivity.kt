package com.example.submissionintermediate.NewStory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submissionintermediate.Api.ApiConfig
import com.example.submissionintermediate.Api.Response.FileUploadResponse
import com.example.submissionintermediate.Data.User
import com.example.submissionintermediate.Data.UserPreference
import com.example.submissionintermediate.Data.ViewModelFactory
import com.example.submissionintermediate.Helper.rotateBitmap
import com.example.submissionintermediate.Helper.uriToFile
import com.example.submissionintermediate.HomePage.MainActivity
import com.example.submissionintermediate.databinding.ActivityNewStoryBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class NewStoryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNewStoryBinding
    private lateinit var newStoryViewModel : NewStoryViewModel
    private var getFile : File? = null
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if (!allPermissionGranted()){
                Toast.makeText(this, "tidak mendapat permission", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionGranted()){
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        setupVieModel()
        cameraButton()
        galleryButton()
        uploadButton()

    }

    private fun setupVieModel(){
        newStoryViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[NewStoryViewModel::class.java]
    }


    private fun cameraButton(){
        binding.btnCamera.setOnClickListener{
            startCamera()
        }
    }

    private fun galleryButton(){
        binding.btnGallery.setOnClickListener{
            openGallery()
        }
    }

    private fun uploadButton(){
        binding.btnUpload.setOnClickListener{
            showLoading(true)
            uploadFileImage()
        }
    }

    private fun startCamera(){
        val intent = Intent(this, CameraActivity::class.java)
        launcherCamera.launch(intent)
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == CAMERA_X_RESULT){
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera =it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(BitmapFactory.decodeFile(getFile?.path), isBackCamera)

            binding.previewImage.setImageBitmap(result)
        }
    }

    private fun openGallery(){
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "choose picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == RESULT_OK){
            val selectedImg : Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            getFile = myFile
            binding.previewImage.setImageURI(selectedImg)
        }
    }

    private fun uploadFileImage(){
        if (getFile != null){
            val file = reduceFileImage(getFile as File)
            val description = "${binding.edtDescription.text}".toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart : MultipartBody.Part =MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)&&checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)){
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location == null){
                        Toast.makeText(this, "location not found", Toast.LENGTH_SHORT).show()
                    } else {
                        val lat = location.latitude
                        val lon = location.longitude

            newStoryViewModel.getUser().observe(this){user ->
                val client = ApiConfig.getApiService().uploadStory(
                    "Bearer ${user.token}",
                    imageMultipart,
                    description,
                    lat,
                    lon
                )
                client.enqueue(object : Callback<FileUploadResponse> {
                    override fun onResponse(
                        call: Call<FileUploadResponse>,
                        response: Response<FileUploadResponse>,
                    ) {
                        if (response.isSuccessful){
                            showLoading(false)
                            val responseBody = response.body()
                            if (responseBody != null && !responseBody.error){
                                Toast.makeText(this@NewStoryActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        } else {
                            Toast.makeText(this@NewStoryActivity, response.message(), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                        showLoading(false)
                        Toast.makeText(this@NewStoryActivity, t.message, Toast.LENGTH_SHORT).show()
                    }

                })
                }
            }
        }
    }
            }

    }

    private fun reduceFileImage(file:File) : File{
        val bitmap =BitmapFactory.decodeFile(file.path)
        var compressQuality =100
        var streamLength : Int

        do {
            val bmpStream =ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -=5
        } while (streamLength > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun showLoading(state: Boolean) {
        if(state) {
            binding.pbMain.visibility = View.VISIBLE
        } else {
            binding.pbMain.visibility = View.GONE
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){permission->
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
}