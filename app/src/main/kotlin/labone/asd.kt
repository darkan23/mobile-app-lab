package labone

import mu.KotlinLogging.logger
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

private const val KEY = "trnsl.1.1.20191203T093154Z.1d3a3cbe6114522d.547af4689e0798826b06d70cbf57568af2e273a0"
private const val PATH_YADNEX_TRANSLATE =
    "https://translate.yandex.net/api/v1.5/tr.json/translate?key=$KEY&lang=ru&text="

class YandexImpl(private val word: String): Yandex {
    private val log = logger{}

    override fun translate(): YandexJson {
        try {
            val response = queryWithParams(word)

            val json = JSONObject(response)
            val text = when (val text1 = json.getFirstElementOrNull("text")) {
                word, null -> "перевод отсутствует"
                else -> text1
            }
            val yandexData = YandexJson(json.getInt("code"), json.getString("lang"), text)
            log.info { "create data: $yandexData" }
            return yandexData
        } catch (e: Exception) {
            log.error { "exception with connect: $e" }
        }
        return YandexJson(text = "Ошибка соединения к сервису. Обратитесь к ...@gmail.com")
    }

    @Throws(Exception::class)
    override fun queryWithParams(word: String): String {
        val urlEncode = URLEncoder.encode(word, "UTF-8")
        val response = URL(PATH_YADNEX_TRANSLATE + urlEncode).readText()
        log.info { "response: $response" }
        return response
    }

    private fun JSONObject.getFirstElementOrNull(key: String): String? {
        val text = this.getJSONArray(key)[1]
        return if (text is String) text.toString() else null
    }
}

data class YandexJson(
    val code: Int? = null,
    val lang: String? = null,
    val text: String
)

interface Yandex {
    fun translate(): YandexJson

    @Throws(Exception::class)
    fun queryWithParams(word: String): String
}