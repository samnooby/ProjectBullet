package com.nooby.projectbullet.daily

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.nooby.projectbullet.bullet.BulletAdapter
import com.nooby.projectbullet.bullet.BulletListener
import com.nooby.projectbullet.database.Bullet
import com.nooby.projectbullet.database.Day
import com.nooby.projectbullet.databinding.DailyPageBinding
import com.nooby.projectbullet.generated.callback.OnClickListener
import java.util.*

class DailyPageAdapter(private val clickListener: DailyPageListener) :
    RecyclerView.Adapter<DailyPageAdapter.ViewHolder>() {

    //The list of days displayed by the pageViewer
    var days = listOf<Day>()
        set(value) {
            field = value
            Log.i("DailyPageAdapter", "Updated days $value")
            notifyDataSetChanged()
        }

    class ViewHolder private constructor(private val binding: DailyPageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val listOrder: List<Long>
            get() {
                val newOrder = binding.day?.bullets?.value?.map { it.bulletId } ?: listOf()
                return if (newOrder == originalOrder) listOf() else newOrder
            }
        val day: Day
            get() {
                return binding.day!!
            }
        private lateinit var originalOrder: List<Long>
        private lateinit var bulletAdapter: BulletAdapter
        private lateinit var touchHelper: ItemTouchHelper

        fun bind(
            day: Day,
            clickListener: DailyPageListener
        ) {
            Log.i("DailyPageAdapter", "Binding page for day $day")
            //Bind the day to the layout and create the bindings
            binding.day = day
            setupTouchHelper()
            bulletAdapter = BulletAdapter(
                BulletListener({ clickListener.onClick(it) },
                    { clickListener.onComplete(it) }, { bullet: Bullet, s: String ->
                        clickListener.onNote(bullet, s)
                    }, { bullet: Bullet, i: Int -> clickListener.onNoteEdit(bullet, i)}),
                touchHelper
            )
            //Adds an observer to the bullet adapter to update with livedata
            day.bullets.observe(itemView.context as LifecycleOwner, Observer {
                it.let {
                    Log.i("DailyPageAdapter", "Daily bullets updated $it")
                    bulletAdapter.bullets = it
                }
            })
            originalOrder = day.bullets.value?.map { it.bulletId } ?: listOf()
            //Sets up the drag controls
            binding.bulletList.adapter = bulletAdapter
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DailyPageBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        private fun setupTouchHelper() {
            touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
            ) {

                override fun isLongPressDragEnabled(): Boolean {
                    return false
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    original: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    //If a task is dragged update the position
                    val sourcePosition = original.adapterPosition
                    val targetPosition = target.adapterPosition
                    if (sourcePosition == targetPosition) {
                        return false
                    }
                    Log.i("DailyPageAdapter", "Moving page to $sourcePosition from $targetPosition")
                    val tmpBullets = day.bullets.value?.toMutableList()
                    Collections.swap(tmpBullets, sourcePosition, targetPosition)
                    bulletAdapter.doesNotify = false
                    day.bullets.value = tmpBullets
                    bulletAdapter.doesNotify = true
                    bulletAdapter.notifyItemMoved(sourcePosition, targetPosition)

                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    TODO("Not yet implemented")
                    //Never called because swipe direction is 0
                }
            })
            touchHelper.attachToRecyclerView(binding.bulletList)
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount() = days.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = days[position]
        holder.bind(item, clickListener)
    }

    private lateinit var currentHolder: ViewHolder

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
//        holder.removeTouchHelper()
        clickListener.onDrag(holder.listOrder, holder.day)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        currentHolder = holder
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        Log.i("DailyPageAdapter", "Recycling page ${holder.day}")
    }

    fun close() {
        clickListener.onDrag(currentHolder.listOrder, currentHolder.day)
    }
}

//DailyPageCallback runs when the page is changed to make the pageviewer infinite
class DailyPagerCallback(private val listener: (Int) -> Unit) : ViewPager2.OnPageChangeCallback() {
    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        Log.i("PageAdapter", "Checking page number got $position")
        when (position) {
            0 -> listener.invoke(PAGE_LIMIT)
            PAGE_LIMIT + 1 -> listener.invoke(1)
        }
    }
}

class DailyPageListener(
    val clickListener: (bullet: Bullet) -> Unit,
    val taskListener: (bullet: Bullet) -> Unit,
    val noteListener: (bullet: Bullet, note: String) -> Unit,
    val editNoteListener: (bullet: Bullet, notePosition: Int) -> Unit,
    val dragListener: (changes: List<Long>, day: Day) -> Unit
) {
    fun onClick(bullet: Bullet) = clickListener(bullet)
    fun onComplete(bullet: Bullet) = taskListener(bullet)
    fun onNote(bullet: Bullet, note: String) = noteListener(bullet, note)
    fun onNoteEdit(bullet: Bullet, notePosition: Int) = editNoteListener(bullet, notePosition)
    fun onDrag(changes: List<Long>, day: Day) = dragListener(changes, day)
}