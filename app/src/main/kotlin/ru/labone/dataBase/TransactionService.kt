package ru.labone.dataBase

import javax.inject.Inject
import java.util.concurrent.Callable

interface TransactionService {
    fun doInTransaction(action: () -> Unit)

    fun <V> doInTransaction(action: () -> V): V
}

internal class TransactionServiceImpl @Inject
constructor(private val database: DataBaseCounters) : TransactionService {
    override fun doInTransaction(action: () -> Unit) {
        database.runInTransaction(action)
    }

    override fun <V> doInTransaction(action: () -> V): V = database.runInTransaction(Callable(action))
}
