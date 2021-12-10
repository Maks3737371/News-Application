package com.maks.newsapp.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

data class NewsResponse(
    val status: String,
    val totalResult: Int,
    val articles: MutableList<Article>,
)

@Entity(tableName = "news_table")
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String,
):Serializable