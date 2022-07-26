package com.example.submissionintermediate.StoryPage

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.submissionintermediate.Data.Story
import com.example.submissionintermediate.Data.User
import com.example.submissionintermediate.Data.UserPreference
import com.example.submissionintermediate.Data.ViewModelFactory
import com.example.submissionintermediate.HomePage.MainActivity
import com.example.submissionintermediate.Login.LoginActivity
import com.example.submissionintermediate.databinding.ActivityStoryBinding

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class StoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setView()

    }

    private fun setView(){
        val story = intent.getParcelableExtra<Story>("story") as Story
        binding. nameTextView.text = story.name
        binding.dateTextView.text = story.createdAt
        binding.descTextView.text = story.description

        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.previewImageView)
    }


}