package labone.profile

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Success
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import labone.mvrx.AssistedViewModelFactory
import labone.mvrx.DiMavericksViewModelFactory
import mu.KotlinLogging.logger
import java.net.URI
import java.time.LocalDate

class ProfileViewModel @AssistedInject constructor(
    @Assisted initialState: ProfileState,
    private val profileRepository: ProfileRepository,
) : MavericksViewModel<ProfileState>(initialState) {

    private val log = logger {}

    init {
        profileRepository.flowCustomerProfile()
            .execute { profile: Async<CustomerProfile> ->
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
        viewModelScope.launch {
            val newDetails = CustomerProfile(
                name = state.name,
                surname = state.surname,
                gender = state.gender,
                birthDate = state.birthDate
            )
            log.info { "newDetails ${state.id}" }
            if (state.id != null) {
                profileRepository.updateProfile(state.id, state.name, state.surname, state.gender, state.birthDate)
            } else {
                profileRepository.saveProfile(newDetails)
            }
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

    fun onPickAvatar(uri: URI) = viewModelScope.launch {
        setState {
            copy(
                photoUri = uri
            )
        }
        profileRepository.updateAvatar(uri)
    }

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<ProfileViewModel, ProfileState> {
        override fun create(initialState: ProfileState): ProfileViewModel
    }

    companion object :
        DiMavericksViewModelFactory<ProfileViewModel, ProfileState>(ProfileViewModel::class.java)
}

data class ProfileState(
    val name: String? = null,
    val surname: String? = null,
    val gender: Gender? = null,
    val birthDate: LocalDate? = null,
    val photoUri: URI? = null,
    val id: Long? = null,
) : MvRxState
