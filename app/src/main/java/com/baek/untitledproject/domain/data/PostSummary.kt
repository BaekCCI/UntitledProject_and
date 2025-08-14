package com.baek.untitledproject.domain.data

import android.net.Uri

data class PostSummary(
    val postId : String,
    val organization : String,
    val title: String,
    val status: String,
    val imgUri: Uri? = null
)
