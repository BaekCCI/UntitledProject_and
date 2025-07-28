package com.baek.untitledproject.domain.data

data class Board(
    val id : String,
    val category : String,
    val title: String,
    val recruitStatus: String,
    val recruitStartDate: String,
    val recruitEndDate: String,
    val content : String,
    val interviewFlag : Boolean,
    val imgUrl: List<String> = emptyList()
)
