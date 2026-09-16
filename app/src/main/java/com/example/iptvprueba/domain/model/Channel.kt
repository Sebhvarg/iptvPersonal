package com.example.iptvprueba.domain.model

data class Channel(
    val id: String,
    val name: String,
    val logoUrl: String?,
    val groupTitle: String,
    val streamUrl: String,
    val resolution: String = "",
    val isLive: Boolean = true
)
