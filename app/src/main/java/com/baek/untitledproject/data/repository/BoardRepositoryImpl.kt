package com.baek.untitledproject.data.repository

import com.baek.untitledproject.data.sample.BoardSampleData
import com.baek.untitledproject.domain.data.BoardSummary
import com.baek.untitledproject.domain.repository.BoardRepository
import javax.inject.Inject

class BoardRepositoryImpl @Inject constructor() : BoardRepository {
    override suspend fun getBoardList(): List<BoardSummary> {
        return BoardSampleData.boardSummaryList
    }
}