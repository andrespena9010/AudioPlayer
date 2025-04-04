package com.exaple.audioplayer.data

data class MediaItems(
    val audioList: List<Language> = listOf()
)

data class Language(
    val mediaTrackGroupId: String,
    val languageIso6392Name: String
)