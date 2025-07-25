package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.data.BoardSummary

interface BoardRepository {
    suspend fun getBoardList() : List<BoardSummary>
}