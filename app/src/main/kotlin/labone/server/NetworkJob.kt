package labone.server

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import mu.KotlinLogging.logger
import javax.inject.Provider
import javax.inject.Singleton

interface NetworkJobSource {
    fun composeNetworkJob(): Completable
}

// TODO ConnectivityManager.NetworkCallback is API 21 interface
private class NetworkJobExecutor(
    private val jobSources: Provider<Set<NetworkJobSource>>
) : DefaultLifecycleObserver {
    private val log = logger {}
    private val jobsDisposable: CompositeDisposable = CompositeDisposable()

    private fun deactivateJobs() {
        log.debug { "Deactivating ${jobsDisposable.size()} network jobs." }
        jobsDisposable.clear()
    }

    // TODO reentrancy
    private fun activateJobs() {
        val jobSources = jobSources.get()
        log.debug { "Activating ${jobSources.size} network jobs." }
        for (jobSource in jobSources) {
            jobSource.composeNetworkJob()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        log.debug { "Job source $jobSource competes." }
                    }

                    override fun onSubscribe(d: Disposable) {
                        jobsDisposable.add(d)
                    }

                    override fun onError(e: Throwable) {
                        log.warn(e) { "Job source $jobSource terminated by error." }
                    }
                })
        }
    }
}

// TODO invent another way for instantiation of NetworkJobExecutor
@Module
object NetworkJobModule {
    @Provides
    @Singleton
    @IntoSet
    fun networkJobExecutor(jobSources: Provider<Set<NetworkJobSource>>): LifecycleObserver =
        NetworkJobExecutor(jobSources)
}
