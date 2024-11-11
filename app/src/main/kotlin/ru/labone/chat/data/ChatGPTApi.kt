package ru.labone.chat.data

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

private const val token =
    "Bearer YOUR_TOKEN"

@Serializable
data class ChatGPTBody(
    val model: String,
    val messages: List<Message>
)

@Serializable
data class ChatCompletionResponse(
    val id: String,
    val choices: List<Choice>,
)

@Serializable
data class Choice(
    val index: Int,
    val message: Message,
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

interface ChatGPTApi {
    @Headers(
        "Content-Type: application/json",
        "Authorization: $token",
    )
    @POST("chat/completions")
    suspend fun completionResponse(@Body body: ChatGPTBody): Response<ChatCompletionResponse>
}
