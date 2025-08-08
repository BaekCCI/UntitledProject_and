package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.data.Board
import com.baek.untitledproject.domain.data.BoardSummary
import com.baek.untitledproject.domain.data.Post
import com.baek.untitledproject.domain.utils.Result

interface BoardRepository {
    suspend fun getBoardList(): Result<List<BoardSummary>>

    suspend fun getBoard(id: String): Result<Board>

    suspend fun searchBoard(keyword: String): Result<List<BoardSummary>>

    suspend fun getPost(postId: String): Result<Post>
}