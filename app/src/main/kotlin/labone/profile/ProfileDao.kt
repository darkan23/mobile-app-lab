package labone.profile

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import io.reactivex.Flowable
import org.threeten.bp.LocalDate
import java.net.URI

@Dao
abstract class ProfileDao {

    @Insert(onConflict = IGNORE)
    abstract fun saveLocal(profile: CustomerProfile): Long

    @Query(
        "UPDATE CustomerProfile " +
                "SET " +
                "name = :name, " +
                "surname = :surname, " +
                "gender = :gender, " +
                "birthDate = :birthDate " +
                "WHERE id = :id"
    )
    abstract fun innerUpdateLocal(
        id: Long,
        name: String?,
        surname: String?,
        gender: Gender?,
        birthDate: LocalDate?
    )

    fun updateProfile(id: Long, name: String?, surname: String?, gender: Gender?, birthDate: LocalDate?) {
        innerUpdateLocal(id, name, surname, gender, birthDate)
    }

    @Query("UPDATE CustomerProfile SET customerAvatar = :avatarUri")
    abstract fun updateAvatar(avatarUri: URI)

    @Query("SELECT * FROM CustomerProfile")
    abstract fun observe(): Flowable<CustomerProfile>
}
