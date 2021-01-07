package com.nooby.projectbullet.bullet

import android.annotation.SuppressLint
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
        @SuppressLint("ClickableViewAccessibility")
        fun bind(
            item: Bullet,
            clickListener: BulletListener
        ) {
            binding.bullet = item
            //Creates the gesture detector to lookout control note text box
            val gestureDetector =
                GestureDetector(itemView.context, GestureListener() {
                    //Show or hide dialog depending of if its already showing
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
            //Sets up the touch and event listeners on the page
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
            binding.newNoteTxt.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    binding.constraintLayout.visibility = View.GONE
                    if (binding.newNoteTxt.text.isNotEmpty()) {
                        clickListener.addNote(item, binding.newNoteTxt.text.toString())
                        binding.newNoteTxt.setText("")
                    }
                }
            }

            binding.executePendingBindings()
            Log.i("BulletAdapter", "Got bullet ${item.bulletNotes.size}")
            //The adapter for the list of notes on the page
            val noteAdapter = BulletNoteAdapter(NoteListener {
                clickListener.editNote(item, it)
            })
            noteAdapter.notes = item.bulletNotes
            binding.bulletNoteList.adapter = noteAdapter


        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                //When the view is created is uses the text_item_view layout and sets the values
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BulletItemViewBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        //GestureListener objects control what happens on the page with each event
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

//BulletListeners listen for events
class BulletListener(
    val clickListener: (bullet: Bullet) -> Unit,
    val taskListener: (bullet: Bullet) -> Unit,
    val noteListener: (bullet: Bullet, note: String) -> Unit,
    val editNoteListener: (bullet: Bullet, notePosition: Int) -> Unit
) {
    fun onClick(bullet: Bullet) = clickListener(bullet)
    fun completeTask(bullet: Bullet) = taskListener(bullet)
    fun addNote(bullet: Bullet, note: String) = noteListener(bullet, note)
    fun editNote(bullet: Bullet, notePosition: Int) = editNoteListener(bullet, notePosition)
}