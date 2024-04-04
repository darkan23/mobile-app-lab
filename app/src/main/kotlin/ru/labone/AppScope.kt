package ru.labone

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class AppScope(
    exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        log.error(exception) { "Unhandled exception in app coroutine scope." }
    },
    extraContext: CoroutineContext = EmptyCoroutineContext,
) : CoroutineScope by CoroutineScope(SupervisorJob() + exceptionHandler + extraContext)

private val log = KotlinLogging.logger(AppScope::javaClass.name)
