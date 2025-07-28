package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.data.Board
import com.baek.untitledproject.domain.data.BoardSummary
import com.baek.untitledproject.domain.utils.Result

interface BoardRepository {
    suspend fun getBoardList() : Result<List<BoardSummary>>

    suspend fun getBoard(id:String) : Result<Board>
}