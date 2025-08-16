package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.data.Board
import com.baek.untitledproject.domain.data.PostSummary
import com.baek.untitledproject.domain.data.Post
import com.baek.untitledproject.domain.utils.Result

interface BoardRepository {
    suspend fun getPostSummaryList(): Result<List<PostSummary>>

    suspend fun getPostById(postId: String): Result<Post>

    suspend fun submitPost(post: Post): Result<String>
}