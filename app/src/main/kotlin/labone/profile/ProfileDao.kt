package labone.profile

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.net.URI
import java.time.LocalDate

@Dao
abstract class ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveLocal(profile: CustomerProfile)

    @Query(
        "UPDATE CustomerProfile " +
            "SET " +
            "name = :name, " +
            "surname = :surname, " +
            "gender = :gender, " +
            "birthDate = :birthDate " +
            "WHERE id = :id"
    )
    abstract suspend fun updateProfile(
        id: Long,
        name: String?,
        surname: String?,
        gender: Gender?,
        birthDate: LocalDate?,
    )

    @Query("UPDATE CustomerProfile SET customerAvatar = :avatarUri")
    abstract suspend fun updateAvatar(avatarUri: URI)

    @Query("SELECT * FROM CustomerProfile")
    abstract fun observe(): Flow<CustomerProfile?>
}
