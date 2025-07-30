package com.baek.untitledproject.domain.utils

//서버에서 받아온 데이터 상태 관리
sealed class Result<out T> {
    data class Success<out T>(val data: T): Result<T>()
    data class Error(val message:String?=null, val throwable: Throwable? = null): Result<Nothing>()
    data object Loading:Result<Nothing>()
}