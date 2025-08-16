package com.baek.untitledproject.data.repository

import android.util.Log
import com.baek.untitledproject.data.remote.PostRemote
import com.baek.untitledproject.data.sample.BoardSampleData
import com.baek.untitledproject.domain.data.Board
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.domain.data.PostSummary
import com.baek.untitledproject.domain.data.Post
import com.baek.untitledproject.domain.repository.BoardRepository
import javax.inject.Inject

class BoardRepositoryImpl @Inject constructor() : BoardRepository {
    override suspend fun getPostSummaryList(): Result<List<PostSummary>> {
        return try {
            val result = PostRemote.getPostSummaryList()
            Result.Success(result)
        } catch (e: Exception) {
            Log.e("BoardRepository", "게시글 리스트 로딩 실패", e)
            Result.Error("게시글 리스트를 불러오는데 실패하였습니다.", e)
        }
    }

    override suspend fun getPostById(postId: String): Result<Post> {
        return try {
            val result = PostRemote.getPostById(postId)
            Result.Success(result)
        } catch (e: Exception) {
            Log.e("BoardRepository", "게시글 로딩 실패", e)
            Result.Error("$postId: 게시글을 불러오는데 실패하였습니다.", e)
        }
    }

    override suspend fun submitPost(post: Post): Result<String> {
        return try {
            val result = PostRemote.uploadPost(post)
            return Result.Success(result)
        } catch (e: Exception) {
            Log.e("BoardRepository", "게시글 저장 실패", e)
            Result.Error("게시글을 저장하는데 실패하였습니다.", e)
        }
    }
}