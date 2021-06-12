package ca.newbys.bullet.database

import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "days")
data class Day(
    @PrimaryKey val date: LocalDate
) {
    @Ignore
    var bullets: MutableLiveData<List<Bullet>> = MutableLiveData()
}
