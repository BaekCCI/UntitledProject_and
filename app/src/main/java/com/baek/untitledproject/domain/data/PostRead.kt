package com.baek.untitledproject.domain.data

import android.net.Uri
import java.time.LocalDate

data class PostRead(
    val postId: String,
    val authorUserId: String,
    val title: String,
    val organization: String,
    val content: String,
    val recruitmentStart: LocalDate,
    val recruitmentEnd: LocalDate?,
    val status: String,

    val hasInterview: Boolean,
    val interviewStart: LocalDate? = null,
    val interviewEnd: LocalDate? = null,
    val interviewLocation: String? = null,//면접 장소

    val requiresName: Boolean = false,
    val requiresStudentId: Boolean = false,
    val requiresDepartment: Boolean = false,
    val requiresGender: Boolean = false,
    val requiresAge: Boolean = false,
    val requiresPhone: Boolean = false,

    val imageUris: List<Uri> = emptyList(), //업로드한 이미지

    val isAuthor: Boolean = false,
    val isApplied: Boolean = false
)
