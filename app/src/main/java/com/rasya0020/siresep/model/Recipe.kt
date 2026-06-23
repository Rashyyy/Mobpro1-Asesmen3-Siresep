package com.rasya0020.siresep.model
data class Recipe(
    val id: Int,
    val judul: String,
    val durasi: String,
    val tingkatKesulitan: String,
    val deskripsi: String,
    val imageUri: String?,
    val isMine: String = "1"
)
