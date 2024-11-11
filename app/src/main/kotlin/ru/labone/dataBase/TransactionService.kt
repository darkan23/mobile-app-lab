package ru.labone.dataBase

import java.util.concurrent.Callable
import javax.inject.Inject

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
