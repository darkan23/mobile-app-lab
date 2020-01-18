package labone.server

import mu.KotlinLogging.logger
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class RetryInterceptor(private val maxAttempts: Int) : Interceptor {
    private val log = logger {}

    init {
        require(maxAttempts > 0) { "maxAttempts must be > 0" }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var response: Response? = null
        var lastException: IOException? = null
        for (attempt in 1..maxAttempts) {
            val request = chain.request()
            try {
                response = chain.proceed(request)
                if (response.isSuccessful || response.isRedirect) {
                    log.debug { "Request success on $attempt attempt" }
                    return response
                } else if (response.code in 500 until 600) {
                    log.info {
                        "Got retryable response code ${response?.code} on $attempt attempt. " +
                                "URL: ${request.url}"
                    }
                } else {
                    log.info {
                        "Got NOT retryable response code ${response?.code} on $attempt attempt. " +
                                "URL: ${request.url}"
                    }
                }
            } catch (ioe: IOException) {
                log.info(ioe) { "IO fail on $attempt attempt. URI: ${request.url}" }
                lastException = ioe
                response = null
            }
        }
        if (response == null) {
            throw lastException!!
        } else {
            return response
        }
    }
}
