package com.example.freezer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ApiViewModel : ViewModel() {
    private val openAIApiService = OpenAIApiService.create()

    // LiveData for the chat response
    private val _chatResponse = MutableLiveData<String>()
    val chatResponse: LiveData<String> = _chatResponse

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun getChatResponseFromAPI(items: String) {
        _isLoading.value = true // Start loading
        viewModelScope.launch {
            try {
                // Define the message list for the Chat API
                val messages = listOf(
                    Message(role = "user", content = "Make recipe from some of these food items: $items")
                )

                // Create a request data object for the Chat API
                val requestData = ChatRequestData(model = "gpt-3.5-turbo", messages = messages)

                // Make the API call
                val response = openAIApiService.getChatResponse(requestData)

                // Update the LiveData with the response
                withContext(Dispatchers.Main) {
                    _chatResponse.value = response.choices.firstOrNull()?.message?.content ?: "No response found"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Update LiveData with error message or handle the error as needed
                    _chatResponse.value = "Error: ${e.message}"
                }
            } finally {
                _isLoading.value = false // Stop loading
            }
        }
    }
}