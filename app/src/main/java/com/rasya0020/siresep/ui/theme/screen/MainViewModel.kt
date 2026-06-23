package com.rasya0020.siresep.ui.theme.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasya0020.siresep.model.Recipe
import com.rasya0020.siresep.network.ApiStatus
import com.rasya0020.siresep.network.RecipeApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {
    var daftarResep = mutableStateOf(emptyList<Recipe>())
        private set

    var statusApi = MutableStateFlow(ApiStatus.LOADING)
        private set

    var pesanError = mutableStateOf<String?>(null)
        private set

    fun ambilDaftarResep() {
        viewModelScope.launch(Dispatchers.IO) {
            statusApi.value = ApiStatus.LOADING
            try {
                daftarResep.value = RecipeApi.service.getRecipe()
                statusApi.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.d("MainViewModel", "Gagal memuat resep: ${e.message}")
                statusApi.value = ApiStatus.FAILED
            }
        }
    }

    fun simpanResep(judul: String, durasi: String, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ambilDaftarResep()
            } catch (e: Exception) {
                Log.d("MainViewModel", "Gagal menyimpan: ${e.message}")
                pesanError.value = "Error: ${e.message}"
            }
        }
    }

    private fun Bitmap.keMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpg".toMediaTypeOrNull(), 0, byteArray.size
        )
        return MultipartBody.Part.createFormData("gambar", "resep.jpg", requestBody)
    }

    fun bersihkanPesan() {
        pesanError.value = null
    }
}