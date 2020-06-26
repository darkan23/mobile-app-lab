package labone.profile

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import org.threeten.bp.LocalDate
import java.net.URI

interface ProfileService {
    fun saveProfile(customerDetails: CustomerProfile): Disposable

    fun updateProfile(id: Long, name: String?, surname: String?, gender: Gender?, birthDate: LocalDate?): Disposable

    fun updateAvatar(avatarUri: URI): Disposable

    fun observeCustomerProfile(): Flowable<CustomerProfile>
}

@Entity
data class CustomerProfile(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val name: String? = null,
    val surname: String? = null,
    val gender: Gender? = null,
    val birthDate: LocalDate? = null,
    val customerAvatar: URI? = null
)

enum class Gender {
    MALE, FEMALE
}

internal class ProfileServiceImpl(private val profileDao: ProfileDao) : ProfileService {

    override fun saveProfile(customerDetails: CustomerProfile): Disposable =
        Completable.fromAction { profileDao.saveLocal(customerDetails) }.subscribeOn(Schedulers.io()).subscribe()

    override fun updateProfile(
        id: Long,
        name: String?,
        surname: String?,
        gender: Gender?,
        birthDate: LocalDate?
    ): Disposable = Completable.fromAction { profileDao.updateProfile(id, name, surname, gender, birthDate) }
        .subscribeOn(Schedulers.io()).subscribe()

    override fun updateAvatar(avatarUri: URI): Disposable =
        Completable.fromAction { profileDao.updateAvatar(avatarUri) }.subscribeOn(Schedulers.io()).subscribe()

    override fun observeCustomerProfile(): Flowable<CustomerProfile> = profileDao.observe()
}
