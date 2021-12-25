package com.example.animeshark

import java.io.Serializable

data class QueryResult(
    val title: String,
    val url: String,
    val img: String
) : Serializable

data class Episode(
    val number: String,
    val url: String
)