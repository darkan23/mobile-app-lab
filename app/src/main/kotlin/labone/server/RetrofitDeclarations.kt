@file:ContextualSerialization(
    Instant::class
)

package labone.server

import io.reactivex.Single
import kotlinx.serialization.ContextualSerialization
import org.threeten.bp.Instant
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT

internal interface RetrofitCounterApi {

    @Headers("Accept: application/json")
    @GET("performances")
    fun performances(): Single<Performance>

    @Headers("Accept: application/json")
    @GET("places/{performId}")
    fun placesForPerformance(): Single<Performance>

    @Headers("Accept: text/plane")
    @GET("place/{performId}/{placeId}")
    fun priceForPlace(): Single<String>

    @POST("performance/{performId}/{placeId}")
    fun reservation()

    @DELETE("performance/{performId}/{placeId}")
    fun scheduling()

    @PUT("performance/{oldPlace}/{newPlace}")
    fun changePlace()
}
