package com.nooby.projectbullet.daily

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.nooby.projectbullet.bullet.BulletAdapter
import com.nooby.projectbullet.bullet.BulletListener
import com.nooby.projectbullet.database.Bullet
import com.nooby.projectbullet.databinding.DailyPageBinding
import com.nooby.projectbullet.generated.callback.OnClickListener

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

        fun bind(
            day: Day,
            clickListener: DailyPageListener
        ) {
            //Bind the day to the layout and create the bindings
            binding.day = day
            val bulletAdapter = BulletAdapter(
                BulletListener({ clickListener.onClick(it) },
                    { clickListener.onComplete(it) })
            )
            //Adds an observer to the bullet adapter to update with livedata
            day.bullets.observe(itemView.context as LifecycleOwner, Observer {
                it.let {
                    Log.i("DailyPageAdapter", "Daily bullets updated $it")
                    bulletAdapter.bullets = it
                }
            })
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount() = days.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = days[position]
        holder.bind(item, clickListener)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
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
    val taskListener: (bullet: Bullet) -> Unit
) {
    fun onClick(bullet: Bullet) = clickListener(bullet)
    fun onComplete(bullet: Bullet) = taskListener(bullet)
}