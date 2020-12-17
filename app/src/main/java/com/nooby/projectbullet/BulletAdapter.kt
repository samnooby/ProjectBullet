package com.nooby.projectbullet

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nooby.projectbullet.database.Bullet
import com.nooby.projectbullet.database.BulletType


class BulletAdapter: RecyclerView.Adapter<BulletAdapter.ViewHolder>() {

    var bullets = listOf<Bullet>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun getItemCount() = bullets.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Tells recycle view how to set the element at each position
        val item = bullets[position]
        holder.bind(holder, item)
    }


    class ViewHolder private constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
        val bulletText: TextView = itemView.findViewById(R.id.txt_view_bullet)
        val bulletIcon: ImageView = itemView.findViewById(R.id.icon_bullet_type)

        //bind prepares the view that is used
        fun bind(
            holder: ViewHolder,
            item: Bullet
        ) {
            holder.bulletText.text = item.message
            //Set image icon
            when (item.BulletType) {
                BulletType.NOTE -> holder.bulletIcon.setImageResource(R.drawable.bullet_icon_note)
                BulletType.INCOMPLETETASK -> holder.bulletIcon.setImageResource(R.drawable.bullet_icon_task)
                BulletType.COMPLETETASK -> holder.bulletIcon.setImageResource(R.drawable.bullet_icon_complete)
                BulletType.EVENT -> holder.bulletIcon.setImageResource(R.drawable.bullet_icon_event)
            }

            holder.bulletIcon.setOnClickListener { v: View ->

            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                //When the view is created is uses the text_item_view layout and sets the values
                val view = LayoutInflater.from(parent.context).inflate(R.layout.bullet_item_view, parent, false)
                return ViewHolder(view)
            }
        }
    }

}