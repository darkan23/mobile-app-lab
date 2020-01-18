package labone.server

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Lazy
import dagger.Module
import dagger.Provides
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.serialization.UnstableDefault
import labone.dataBase.TransactionService
import mu.KotlinLogging.logger
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.net.NoRouteToHostException
import java.net.UnknownHostException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private const val CONNECTION_TIMEOUT = 60 // in seconds
private const val READ_TIMEOUT = 60 // in seconds
private const val WRITE_TIMEOUT = 60 // in seconds

@Module
class PerformancesApiModule(baseUrl: String, private val debug: Boolean) {

    private val performanceBaseUrl = baseUrl.toHttpUrl()

    @Provides
    @Singleton
    internal fun okHttpClient(
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(RetryInterceptor(3))
            addInterceptor(HttpLoggingInterceptor().apply {
                level = if (debug) Level.BODY else Level.BASIC
            })
            connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
        }.build()
    }

    @UnstableDefault
    @Singleton
    @Provides
    internal fun counterApi(
        receivers: Lazy<Set<@JvmSuppressWildcards ChangesReceiver>>,
        transactionService: TransactionService,
        apiRepository: ApiRepository,
        client: OkHttpClient
    ): CountersApi {
        val contentType = "application/json; charset=utf-8".toMediaType()
        val retrofit = Retrofit.Builder()
            .baseUrl(performanceBaseUrl)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(YodhaJson.asConverterFactory(contentType))
            .build()
        return CountersApiImpl(
            {
                receivers.get()
                    .sortedWith(compareBy<ChangesReceiver> { it.priority }.thenBy { it.hashCode() })
            },
            transactionService,
            apiRepository,
            retrofit.create(RetrofitCounterApi::class.java)
        )
    }
}

internal class AuthorizationFailedException(message: String, cause: Throwable? = null) :
    IOException(message, cause)

private class CountersApiImpl(
    private val getReceivers: () -> Iterable<ChangesReceiver>,
    private val transactionService: TransactionService,
    private val apiRepository: ApiRepository,
    private val retrofitApi: RetrofitCounterApi
) : CountersApi {
    private val log = logger {}

    private val continuationScheduler by lazy { Schedulers.from(Executors.newSingleThreadExecutor()) }
    private val continuationTokenSubject by lazy {
        BehaviorSubject.createDefault(listOfNotNull(apiRepository.loadContinuationToken()))
    }

    override fun counter(): Single<Performance> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun payment(payment: Payment): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun processChanges(changes: Changes) {
        transactionService.doInTransaction {
            for (receiver in getReceivers()) {
                receiver.consume(changes)
            }
        }
    }

    private fun handleServerError(throwable: Throwable) {
        log.warn(throwable) {
            when (throwable) {
                is HttpException -> "Bad server response: ${throwable.code()} ${throwable.message()}"
                is AuthorizationFailedException -> "Server auth failed."
                is UnknownHostException, is NoRouteToHostException -> "Server unreachable."
                is IOException -> "Server IO failed"
                else -> "Unknown server interaction error"
            }
        }
        // TODO special "No internet" status if server unreachable
    }

    private inline fun withContinuation(crossinline call: (String?) -> Single<Changes>): Completable =
        continuationTokenSubject
            .firstOrError()
            .subscribeOn(continuationScheduler)
            .flatMap { token ->
                log.debug { "Preparing call with continuation token: $token" }
                call(token.firstOrNull()).doAfterSuccess { changes ->
                    processChanges(changes)
                    val newToken = listOfNotNull(changes.continuationToken)
                    continuationTokenSubject.onNext(newToken)
                    apiRepository.saveContinuationToken(newToken.firstOrNull())
                    log.debug { "Continuation token updated: $newToken" }
                }.doOnError {
                    handleServerError(it)
                    log.warn { "Continuation token reused because of error: $token" }
                    continuationTokenSubject.onNext(token)
                }
            }.ignoreElement()
}
