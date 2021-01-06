package com.nooby.projectbullet.bullet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nooby.projectbullet.database.Bullet
import com.nooby.projectbullet.databinding.BulletItemViewBinding


class BulletAdapter(private val clickListener: BulletListener) :
    RecyclerView.Adapter<BulletAdapter.ViewHolder>() {

    //The list of items that is displayed by the recyclerview
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
        holder.bind(item, clickListener)
    }


    class ViewHolder private constructor(private val binding: BulletItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        //bind prepares the view that is used
        fun bind(
            item: Bullet,
            clickListener: BulletListener
        ) {
            binding.bullet = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                //When the view is created is uses the text_item_view layout and sets the values
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BulletItemViewBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }
}

class BulletListener(val clickListener: (bullet: Bullet) -> Unit) {
    fun onClick(bullet: Bullet) = clickListener(bullet)
}