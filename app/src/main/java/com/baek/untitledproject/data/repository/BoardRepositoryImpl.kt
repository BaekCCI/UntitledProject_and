package com.baek.untitledproject.data.repository

import android.util.Log
import com.baek.untitledproject.data.model.mapper.toDomain
import com.baek.untitledproject.data.sample.BoardSampleData
import com.baek.untitledproject.domain.data.Board
import com.baek.untitledproject.domain.utils.Result
import com.baek.untitledproject.domain.data.BoardSummary
import com.baek.untitledproject.domain.data.Post
import com.baek.untitledproject.domain.repository.BoardRepository
import javax.inject.Inject

class BoardRepositoryImpl @Inject constructor() : BoardRepository {
    override suspend fun getBoardList(): Result<List<BoardSummary>> {
        return try {
            Result.Success(BoardSampleData.boardSummaryList)
        } catch (e: Exception) {
            Log.e("BoardRepository", "게시글 리스트 로딩 실패", e)
            Result.Error("게시글 리스트를 불러오는데 실패하였습니다.", e)
        }
    }

    override suspend fun getBoard(id: String): Result<Board> {
        return try {
            val board = BoardSampleData.boardList.find { it.id == id }
            if (board != null) {
                Result.Success(board)
            } else {
                Result.Error("게시글이 존재하지 않습니다.")
            }
        } catch (e: Exception) {
            Log.e("BoardRepository", "게시글 로딩 실패", e)
            Result.Error("$id: 게시글을 불러오는데 실패하였습니다.", e)
        }
    }

    override suspend fun searchBoard(keyword: String): Result<List<BoardSummary>> {
        return try {

            val rawBoard = BoardSampleData.boardSummaryList //서버에서 받아온 데이터

            //제목, 단체명 기준 필터링
            val filteredBoard = rawBoard.filter {
                it.title.contains(keyword) || it.category.contains(keyword)
            }

            Log.d("BoardRepository", filteredBoard.joinToString { it.title })
            Result.Success(filteredBoard)

        } catch (e: Exception) {
            Log.e("BoardRepository", "게시글 검색 실패", e)
            Result.Error("게시글 검색에 실패하였습니다.", e)
        }
    }

    override suspend fun getPost(postId: String): Result<Post> {
        return try {
            val post = BoardSampleData.postList.find { it.post_id == postId }
            val images = BoardSampleData.images
            val interviewSlot = BoardSampleData.interviewSlots
            val customQuestions = BoardSampleData.customQuestions

            if (post == null) {
                return Result.Error("게시글이 존재하지 않습니다.")
            }
            val postDetail = post.toDomain(images, interviewSlot, customQuestions)
            Result.Success(postDetail)
        } catch (e: Exception) {
            Log.e("BoardRepository", "게시글 로딩 실패", e)
            Result.Error("$postId: 게시글을 불러오는데 실패하였습니다.", e)
        }
    }

    override suspend fun submitPost(postId: String?): Result<String> {
        return try {
            //TODO: 파이어베이스에 업로드 작업
            return Result.Success("postId")
        } catch (e: Exception) {
            Log.e("BoardRepository", "게시글 저장 실패", e)
            Result.Error("게시글을 저장하는데 실패하였습니다.", e)
        }
    }
}