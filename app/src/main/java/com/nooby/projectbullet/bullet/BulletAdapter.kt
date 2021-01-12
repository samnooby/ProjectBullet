package com.nooby.projectbullet.bullet

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.Visibility
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import androidx.core.view.marginTop
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.nooby.projectbullet.database.Bullet
import com.nooby.projectbullet.database.BulletWithTag
import com.nooby.projectbullet.databinding.BulletItemViewBinding
import java.text.FieldPosition
import java.util.*
import kotlin.concurrent.schedule


class BulletAdapter(private val clickListener: BulletListener, private val dragHelper: ItemTouchHelper) :
    RecyclerView.Adapter<BulletAdapter.ViewHolder>() {

    //Whether or not to notify the adapter when the data set changes(Used for dragging)
    var doesNotify = true
    var movingUp = false
    //The list of items that is displayed by the recyclerview
    var bullets = listOf<BulletWithTag>()
        set(value) {
            field = value
            if (doesNotify) {
                Log.i("BulletAdapter","Notifying of update")
                notifyDataSetChanged()
            }
        }

    //used to determine the position of newly added bullets
    private var lastInsert: Int = 0

    override fun getItemCount() = bullets.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder.from(parent)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Tells recycle view how to set the element at each position
        val item = bullets[position]
        holder.bind(item, clickListener)
        //Sets up the drag icon
        holder.dragImage.setOnTouchListener{ view: View, motionEvent: MotionEvent ->
            if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                dragHelper.startDrag(holder)
            }
            false
        }
    }

    class ViewHolder private constructor(private val binding: BulletItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val dragImage = binding.bulletDragIcon

        //bind prepares the view that is used
        @SuppressLint("ClickableViewAccessibility")
        fun bind(
            item: BulletWithTag,
            clickListener: BulletListener
            ) {
            binding.bullet = item.bullet
            setupEventHandlers(clickListener, item.bullet)

            binding.executePendingBindings()
            Log.i("BulletAdapter", "Got bullet ${item.bullet.bulletNotes.size}")
            //The adapter for the list of notes on the page
            val noteAdapter = BulletNoteAdapter(NoteListener {
                clickListener.editNote(item.bullet, it)
            })
            noteAdapter.notes = item.bullet.bulletNotes
            binding.bulletNoteList.adapter = noteAdapter
            binding.newNoteTxt.imeOptions = EditorInfo.IME_ACTION_NONE

        }

        private fun setupEventHandlers(
            clickListener: BulletListener,
            item: Bullet
        ) {
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
            //Sets up root touch listener
            binding.root.setOnTouchListener { _, event ->
                gestureDetector.onTouchEvent(event)
            }
            //Completes the bullet if it is a task or event
            binding.iconBulletType.setOnClickListener {
                Log.i("BulletAdapter", "Testero")
                clickListener.completeTask(item)
            }

            //Adds a new note if there is text in the new note textbox
            binding.newNoteBtn.setOnClickListener {
                Log.i("BulletAdapter", "New note button clicked")
                if (binding.newNoteTxt.text.isNotEmpty()) {
                    clickListener.addNote(item, binding.newNoteTxt.text.toString())
                    binding.newNoteTxt.setText("")
                }
                binding.newNoteTxt.clearFocus()
            }
            binding.newNoteTxt.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    if (binding.newNoteTxt.text.isNotEmpty()) {
                        clickListener.addNote(item, binding.newNoteTxt.text.toString())
                        binding.newNoteTxt.setText("")
                    }
                    binding.constraintLayout.visibility = View.GONE
                }
            }

            //Enterkey adds a new note if the text box is not empty
            binding.newNoteTxt.setOnKeyListener { _, keyCode, event ->
                Log.i("BulletAdapter", "Pressed")
                when {
                    ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_DOWN)) -> {
                        Log.i("BulletAdapter", "Enter key pressed")
                        val imm =
                            binding.newNoteTxt.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(binding.newNoteTxt?.windowToken, 0)
                        if (binding.newNoteTxt.text.isNotEmpty()) {
                            clickListener.addNote(item, binding.newNoteTxt.text.toString())
                            binding.newNoteTxt.setText("")
                        }
                        binding.newNoteTxt.clearFocus()
                        return@setOnKeyListener true
                    }
                    else -> false
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

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        movingUp = holder.adapterPosition <= lastInsert
        lastInsert = holder.adapterPosition
        Log.i("BulletAdapter", "Last insert changed to $lastInsert")
    }

    fun getCurrentInsertItem(): Int {
        if (bullets.size < 5) {
            return 0
        }
        return when (movingUp) {
            true -> lastInsert + 1
            false -> lastInsert -3
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