package com.rasya0020.siresep.model

import com.squareup.moshi.Json

data class Recipe(
    val id: Int,
    val judul: String,
    val durasi: String,
    val tingkatKesulitan: String,
    val imageUri: String?,
    val isMine: String = "1"
)
