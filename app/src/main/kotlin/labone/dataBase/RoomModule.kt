package labone.dataBase

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.labone.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import labone.counters.PerformanceDao
import javax.inject.Singleton

/**
 * @author rbarykin
 * @since 13.07.16.
 */
@Module
class RoomModule @JvmOverloads constructor(
    private val inMemory: Boolean = false,
    private val dbName: String = "counters_advanced.db"
) {

    @Provides
    @Singleton
    fun provideYodhaDatabase(
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
        // TODO возможно впоследствии убрать?
        if (!BuildConfig.DEBUG) {
            fallbackToDestructiveMigration()
        }
    }.build()

    @Provides
    @ElementsIntoSet
    internal fun defaultCallbacks() = emptySet<RoomDatabase.Callback>()

    @Provides
    fun provideSubscriptionDao(db: DataBaseCounters): PerformanceDao {
        return db.countersDao()
    }
}
