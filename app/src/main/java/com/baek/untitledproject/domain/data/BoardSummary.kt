package com.baek.untitledproject.domain.data

data class BoardSummary(
    val id : String,
    val category : String,
    val title: String,
    val recruitStatus: String,
    val imgUrl: String? = null
)
