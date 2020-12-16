package com.nooby.projectbullet

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nooby.projectbullet.database.Bullet

class TextItemViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)

class BulletAdapter: RecyclerView.Adapter<TextItemViewHolder>() {

    var bullets = listOf<Bullet>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun getItemCount() = bullets.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        //When the view is created is uses the text_item_view layout and sets the values
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.text_item_view, parent, false) as TextView
        Log.i("BulletAdapter", "Created Recycle View")
        return TextItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        //Tells recycle view how to set the element at each position
        val item = bullets[position]
        holder.textView.text = item.name
    }

}