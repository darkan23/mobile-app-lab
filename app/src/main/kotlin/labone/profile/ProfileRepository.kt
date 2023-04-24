package labone.profile

import kotlinx.coroutines.flow.Flow
import java.net.URI
import java.time.LocalDate

interface ProfileRepository {
    suspend fun saveProfile(customerDetails: CustomerProfile)

    suspend fun updateProfile(id: Long, name: String?, surname: String?, gender: Gender?, birthDate: LocalDate?)

    suspend fun updateAvatar(avatarUri: URI)

    fun flowCustomerProfile(): Flow<CustomerProfile?>
}

internal class ProfileRepositoryImpl(
    private val profileDao: ProfileDao,
) : ProfileRepository {

    override suspend fun saveProfile(customerDetails: CustomerProfile) = profileDao.saveLocal(customerDetails)

    override suspend fun updateProfile(
        id: Long,
        name: String?,
        surname: String?,
        gender: Gender?,
        birthDate: LocalDate?,
    ) = profileDao.updateProfile(id, name, surname, gender, birthDate)

    override suspend fun updateAvatar(avatarUri: URI) = profileDao.updateAvatar(avatarUri)

    override fun flowCustomerProfile(): Flow<CustomerProfile?> = profileDao.observe()
}
