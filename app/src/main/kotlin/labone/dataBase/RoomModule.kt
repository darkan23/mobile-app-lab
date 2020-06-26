package labone.dataBase

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import labone.counters.PerformanceDao
import labone.news.data.NewsDao
import labone.profile.ProfileDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RoomModule @JvmOverloads constructor(
    private val inMemory: Boolean = false,
    private val dbName: String = "counters_advanced.db"
) {

    @Provides
    @Singleton
    fun provideDatabase(
        application: Application,
        callbacks: Set<@JvmSuppressWildcards RoomDatabase.Callback>
    ): DataBaseCounters = if (inMemory) {
        Room.inMemoryDatabaseBuilder(application, DataBaseCounters::class.java)
    } else {
        Room.databaseBuilder(application, DataBaseCounters::class.java, dbName)
    }.apply {
        for (callback in callbacks) {
            addCallback(callback)
        }
    }.build()

    @Provides
    @ElementsIntoSet
    internal fun defaultCallbacks() = emptySet<RoomDatabase.Callback>()

    @Provides
    fun provideSubscriptionDao(db: DataBaseCounters): PerformanceDao {
        return db.countersDao()
    }

    @Provides
    fun provideProfileDao(db: DataBaseCounters): ProfileDao {
        return db.profileDao()
    }

    @Provides
    fun provideNewsDao(db: DataBaseCounters): NewsDao {
        return db.newsDao()
    }
}
