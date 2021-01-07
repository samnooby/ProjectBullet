package com.nooby.projectbullet.bullet

import android.content.Context
import android.opengl.Visibility
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
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
                GestureDetector(itemView.context, GestureListener() {
                    when (it) {
                        0 -> {
                            if (binding.constraintLayout.visibility == View.GONE) {
                                binding.constraintLayout.visibility = View.VISIBLE
                                binding.newNoteTxt.requestFocus()
                            } else {
                                Log.i("BulletAdapter", "Clicking listener")
                                clickListener.onClick(item)
                                binding.constraintLayout.visibility = View.GONE
                            }
                        }
                        1 -> {
                            clickListener.onClick(item)
                            binding.constraintLayout.visibility = View.GONE
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
            binding.newNoteBtn.setOnClickListener {
                Log.i("BulletAdapter", "New note button clicked")
                binding.newNoteTxt.clearFocus()
            }
            binding.executePendingBindings()
            Log.i("BulletAdapter", "Got bullet ${item.bulletNotes.size}")
            val noteAdapter = BulletNoteAdapter()
            noteAdapter.notes = item.bulletNotes
            binding.bulletNoteList.adapter = noteAdapter
            binding.newNoteTxt.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    binding.constraintLayout.visibility = View.GONE
                    if (binding.newNoteTxt.text.isNotEmpty()) {
                        clickListener.addNote(item, binding.newNoteTxt.text.toString())
                        binding.newNoteTxt.setText("")
                    }
                }
            }

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
            val visibilityListener: (Int) -> (Unit)
        ) : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                visibilityListener(0)
                return true
            }

            override fun onDoubleTap(e: MotionEvent?): Boolean {
                visibilityListener(1)
                return true
            }
        }

    }
}

class BulletListener(
    val clickListener: (bullet: Bullet) -> Unit,
    val taskListener: (bullet: Bullet) -> Unit,
    val noteListener: (bullet: Bullet, note: String) -> Unit
) {
    fun onClick(bullet: Bullet) = clickListener(bullet)
    fun completeTask(bullet: Bullet) = taskListener(bullet)
    fun addNote(bullet: Bullet, note: String) = noteListener(bullet, note)
}