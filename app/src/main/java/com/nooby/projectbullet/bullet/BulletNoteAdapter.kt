package com.nooby.projectbullet.bullet

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.nooby.projectbullet.databinding.BulletNoteViewBinding

class BulletNoteAdapter(private val clickListener: NoteListener) : RecyclerView.Adapter<BulletNoteAdapter.ViewHolder>() {

    var notes = listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notes[position]
        holder.bind(item, position, clickListener)
    }

    class ViewHolder private constructor(private val binding: BulletNoteViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        //Prepares the note to be shown
        fun bind(
            item: String,
            position: Int,
            clickListener: NoteListener
        ) {
            Log.i("BulletNoteAdapter", "Making note $item")
            binding.noteTxt.text = item
            binding.noteTxt.setOnClickListener {
                clickListener.onClick(position)
            }
        }

        //Gets the view for the current item in the list and inflating it using the bulletnoteviewbinding
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BulletNoteViewBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

}

class NoteListener(
    val noteListener: (notePosition: Int) -> Unit
) {
    fun onClick(notePosition: Int) = noteListener(notePosition)
}