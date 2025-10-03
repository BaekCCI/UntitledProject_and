package com.baek.untitledproject.data.model

import com.google.firebase.Timestamp
import java.net.URL

data class PostImageResponse(
    val image_id: String = "",
    val post_id: String = "",
    val image_url: String = "",
    val image_order: Int = 0,
    val created_at: Timestamp? = null
)