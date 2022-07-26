package com.example.submissionintermediate.HomePage

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionintermediate.Data.Story
import com.example.submissionintermediate.StoryPage.StoryActivity
import com.example.submissionintermediate.databinding.ItemStoryBinding

class StoryAdapter:
    PagingDataAdapter<Story, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    private var storyList: PagingData<Story>? = null

    fun setStoryList(storyList: PagingData<Story>){
        this.storyList = storyList
    }

    class StoryViewHolder (private val binding : ItemStoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(data: Story){

            itemView.setOnClickListener{
                val intent = Intent(itemView.context, StoryActivity::class.java)
                intent.putExtra("story", data)

                itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity).toBundle())
            }

            binding.apply {
                tvName.text = data.name
                tvDescription.text = data.description

                Glide.with(itemView)
                    .load(data.photoUrl)
                    .into(ivPhoto)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null){
            holder.bind(data)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}