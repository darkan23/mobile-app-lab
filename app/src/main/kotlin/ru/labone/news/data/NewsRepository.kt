package ru.labone.news.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.labone.AppScope
import java.io.IOException

interface NewsRepository {
    fun flowNews(): Flow<List<News>>

    fun saveNews(news: News)

    suspend fun testPush()
}

internal class NewsRepositoryImpl(
    private val appScope: AppScope,
    private val newsDao: NewsDao,
    private val okHttpClient: OkHttpClient,
) : NewsRepository {

    override fun flowNews(): Flow<List<News>> = newsDao.flowNews()

    override fun saveNews(news: News) {
        appScope.launch(Dispatchers.IO) {
            newsDao.saveLocal(news)
        }
    }

    override suspend fun testPush() {
        withContext(Dispatchers.IO) {

            val json = """
        {
            "message": {
                "token": "RAKTBi19EJ2GhwShU2xHn40nHRraFkzF",
                "notification": {
                    "body": "This is a notification message!",
                    "title": "Message",
                    "channel": "Message",
                    "image": "https://avatars.mds.yandex.net/i?id=5c17c4040d6734470699b94f77e13bfac077193d-5219011-images-thumbs&n=13",
                    "click_action": "https://yandex.ru/images/search?from=tabbar&text=image/",
                    "click_action_type": 0
                }
            }
        }
    """.trimIndent()

            val requestBody = json.toRequestBody("application/json".toMediaType())
            try {
                val newRequest = Request.Builder().apply {
                    url("https://vkpns.rustore.ru/v1/projects/jyqHM62fkzivo9Q42d5jS4Ig3YTPg1EV/messages:send")
                    addHeader("content-type", "application/json")
                    addHeader(
                        "Authorization",
                        "Bearer s7f4VLan3eSAAqmyELKDvIGZCpJWeJf7wcUPBAJ7hN2Hm91xg90paSwguC9l1-z5"
                    )
                    post(requestBody)
                }.build()
                okHttpClient.newCall(newRequest).enqueue(
                    object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            e.printStackTrace()
                        }

                        override fun onResponse(call: Call, response: Response) {
                            response.use {
                                if (!it.isSuccessful) {
                                    println("FUCK  Error: ${it.code} ${it.message}")
                                    println("FUCK Response body: ${it.body?.string()}")
                                    throw IOException("Unexpected code $it")
                                }

                                val responseBody = it.body?.string()
                                println(responseBody)
                            }
                        }
                    }
                )
            } catch (e: IOException) {
                println("FUCK_ERROR $e")
            }
        }
    }
}
