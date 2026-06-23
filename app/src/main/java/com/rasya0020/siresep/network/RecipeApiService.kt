package com.rasya0020.siresep.network

import com.rasya0020.siresep.model.Recipe
import com.rasya0020.siresep.model.OpStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://masak-apa.tomorisakura.vercel.app/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface RecipeApiService {
    @GET("api/recipes")
    suspend fun getRecipe(
        @Header("Authorization") userId: String
    ): List<Recipe>

    @Multipart
    @POST("api/recipes")
    suspend fun postRecipe(
        @Header("Authorization") userId: String,
        @Part("judul") judul: RequestBody,
        @Part("durasi") durasi: RequestBody,
        @Part image: MultipartBody.Part
    ): OpStatus

    @DELETE("api/recipes")
    suspend fun deleteRecipe(
        @Header("Authorization") userId: String,
        @Query("id") id: String,
        @Query("action") action: String
    ): OpStatus
}

object RecipeApi {
    val service: RecipeApiService by lazy {
        retrofit.create(RecipeApiService::class.java)
    }
    fun getRecipeUrl(imageId: String): String {
        return "${BASE_URL}image.php?id=$imageId"
    }
}

enum class ApiStatus { LOADING, SUCCESS, FAILED }