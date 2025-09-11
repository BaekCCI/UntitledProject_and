package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.data.PostSummary
import com.baek.untitledproject.domain.data.Post
import com.baek.untitledproject.domain.data.PostRead
import com.baek.untitledproject.domain.data.PostWrite
import com.baek.untitledproject.domain.data.User
import com.baek.untitledproject.domain.utils.Result

interface BoardRepository {
    suspend fun getPostSummaryList(): Result<List<PostSummary>>

    suspend fun getPostById(postId: String): Result<Post>

    suspend fun submitPost(post: PostWrite, user: User): Result<String>

    suspend fun getPostForRead(postId: String, userId: String? = null): Result<PostRead>

    suspend fun getPostForEdit(postId: String): Result<PostWrite>

    suspend fun editPost(post: PostWrite): Result<String>

    suspend fun deletePost(postId: String): Result<Unit>
}