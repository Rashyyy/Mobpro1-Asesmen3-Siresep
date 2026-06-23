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

private const val BASE_URL = "https://siresep-production.up.railway.app/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface RecipeApiService {
    @GET("resep.php")
    suspend fun getRecipe(
        @Header("Authorization") userEmail: String
    ): List<Recipe>

    @Multipart
    @POST("resep.php")
    suspend fun postRecipe(
        @Header("Authorization") userEmail: String,
        @Part("judul") judul: RequestBody,
        @Part("durasi") durasi: RequestBody,
        @Part("tingkat_kesulitan") tingkatKesulitan: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part image: MultipartBody.Part
    ): OpStatus

    @Multipart
    @POST("resep.php")
    suspend fun updateRecipe(
        @Header("Authorization") userEmail: String,
        @Part("id") id: RequestBody,
        @Part("judul") judul: RequestBody,
        @Part("durasi") durasi: RequestBody,
        @Part("tingkat_kesulitan") tingkatKesulitan: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): OpStatus

    @DELETE("resep.php")
    suspend fun deleteRecipe(
        @Header("Authorization") userEmail: String,
        @Query("id") id: String,
    ): OpStatus
}

object RecipeApi {
    val service: RecipeApiService by lazy {
        retrofit.create(RecipeApiService::class.java)
    }
}

enum class ApiStatus { LOADING, SUCCESS, FAILED }