package ca.newbys.bullet.database

import androidx.room.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


//The different type of bullets
enum class BulletType {
    NOTE, INCOMPLETE, COMPLETE
}

//The heart of the application, bullets hold notes and tasks that need to be complete
@Entity(tableName = "bullets")
data class Bullet(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "create_date") val createDate: LocalDateTime = LocalDateTime.now(),

    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "bullet_date") var bulletDate: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "bullet_type") var bulletType: BulletType = BulletType.NOTE,
    @ColumnInfo(name = "bullet_notes") var bulletNotes: List<String> = listOf()
)