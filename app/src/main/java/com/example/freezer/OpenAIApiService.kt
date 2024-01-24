package com.example.freezer

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit


val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS) // Connection Timeout
    .readTimeout(30, TimeUnit.SECONDS)     // Read Timeout
    .writeTimeout(30, TimeUnit.SECONDS)    // Write Timeout
    .build()
interface OpenAIApiService {
    @Headers("Authorization: Bearer APIKEY")
    @POST("/v1/chat/completions")
    suspend fun getChatResponse(@Body requestData: ChatRequestData): ChatResponseData

    companion object {
        fun create(): OpenAIApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.openai.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
            return retrofit.create(OpenAIApiService::class.java)
        }
    }
}

data class ChatRequestData(
    val model: String,
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: String
)

data class ChatResponseData(val choices: List<Choice>)
data class Choice(val message: Message)