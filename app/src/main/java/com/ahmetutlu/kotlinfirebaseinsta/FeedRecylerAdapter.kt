package com.ahmetutlu.kotlinfirebaseinsta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ahmetutlu.model.Post
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

//recyclerView için adaptör oluşturduk(sınıf),sonra RecyclerView sınıfından kalıtım alıyoruz(RecyclerView.Adapter
class FeedRecyclerAdapter(private val postList : ArrayList<Post>) : RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {

    class PostHolder(val binding: RecyclerViewRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerViewRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {//viewların içeriğinde ne olduğunu açıklicaz
        holder.recyclerEmailText.text = postList.get(position).email
        holder.recyclerCommentText.text = postList.get(position).comment
        Picasso.get().load(postList[position].dowloadUrl).into(holder.recyclerImageView)


    }

    override fun getItemCount(): Int {//kaç tane recylerview olduğunu söylicez
        return postList.size

    }
}