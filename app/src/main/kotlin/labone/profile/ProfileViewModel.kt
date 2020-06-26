package labone.profile

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.BuildConfig
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Success
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import labone.mvrx.AssistedViewModelFactory
import labone.mvrx.DaggerViewModelFactory
import mu.KotlinLogging.logger
import org.threeten.bp.LocalDate
import java.net.URI

class ProfileViewModel @AssistedInject constructor(
    @Assisted initialState: ProfileState,
    private val profileService: ProfileService
) : BaseMvRxViewModel<ProfileState>(initialState, BuildConfig.DEBUG) {

    private val log = logger {}

    init {
        profileService.observeCustomerProfile().toObservable().execute { profile: Async<CustomerProfile> ->
            if (profile is Success) {
                val details = profile()
                log.info { "allDetails $details" }
                copy(
                    id = details.id,
                    name = details.name,
                    surname = details.surname,
                    gender = details.gender,
                    birthDate = details.birthDate,
                    photoUri = details.customerAvatar
                )
            } else {
                this
            }
        }
    }


    fun onDone() = withState { state ->
        val newDetails = CustomerProfile(
            name = state.name,
            surname = state.surname,
            gender = state.gender,
            birthDate = state.birthDate
        )
        log.info { "newDetails ${state.id}" }
        if (state.id != null) {
            profileService.updateProfile(state.id, state.name, state.surname, state.gender, state.birthDate)
        } else {
            profileService.saveProfile(newDetails)
        }
    }

    fun changeName(name: String?) = setState { copy(name = name) }

    fun changeSurname(surName: String?) = setState { copy(surname = surName) }

    fun changeGender(gender: Gender?) = setState { copy(gender = gender) }

    fun changeBirthDate(birthDate: LocalDate) {
        val yesterday = LocalDate.now().minusDays(1)
        val effectiveDate = birthDate.takeIf { birthDate.isBefore(yesterday) } ?: yesterday
        setState {
            copy(birthDate = effectiveDate)
        }
    }

    fun onPickAvatar(uri: URI) = profileService.updateAvatar(uri)

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<ProfileViewModel, ProfileState>

    companion object : DaggerViewModelFactory<ProfileViewModel, ProfileState>(
        ProfileViewModel::class
    )
}

data class ProfileState(
    val name: String? = null,
    val surname: String? = null,
    val gender: Gender? = null,
    val birthDate: LocalDate? = null,
    val photoUri: URI? = null,
    val id: Long? = null
) : MvRxState
