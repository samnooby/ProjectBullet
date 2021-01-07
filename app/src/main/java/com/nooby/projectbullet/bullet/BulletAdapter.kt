package com.nooby.projectbullet.bullet

import android.opengl.Visibility
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.nooby.projectbullet.database.Bullet
import com.nooby.projectbullet.databinding.BulletItemViewBinding
import java.util.*
import kotlin.concurrent.schedule


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
            val gestureDetector =
                GestureDetector(itemView.context, GestureListener(item, clickListener) {
                    if (binding.constraintLayout.visibility == View.VISIBLE) {
                        binding.constraintLayout.visibility = View.GONE
                    } else {
                        if (it == 0) {
                            binding.constraintLayout.visibility = View.VISIBLE
                        }
                    }
                })
            binding.root.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
            }
            binding.iconBulletType.setOnClickListener {
                Log.i("BulletAdapter", "Testero")
                clickListener.completeTask(item)
            }
            binding.executePendingBindings()
            Log.i("BulletAdapter", "Got bullet $item")
            val noteAdapter = BulletNoteAdapter(item.bulletNotes)
            binding.bulletNoteList.adapter = noteAdapter
            binding.constraintLayout.visibility = View.GONE
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                //When the view is created is uses the text_item_view layout and sets the values
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BulletItemViewBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        private class GestureListener(
            val item: Bullet,
            val listener: BulletListener,
            val visibilityListener: (Int) -> (Unit)
        ) : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                visibilityListener(0)
                return true
            }

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                visibilityListener(1)
                listener.onClick(item)
                return true
            }
        }

    }
}

class BulletListener(
    val clickListener: (bullet: Bullet) -> Unit,
    val taskListener: (bullet: Bullet) -> Unit
) {
    fun onClick(bullet: Bullet) = clickListener(bullet)
    fun completeTask(bullet: Bullet) = taskListener(bullet)
}