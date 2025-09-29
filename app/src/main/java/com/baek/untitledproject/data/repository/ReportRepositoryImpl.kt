package com.baek.untitledproject.data.repository

import android.util.Log
import com.baek.untitledproject.data.remote.ReportRemote
import com.baek.untitledproject.domain.data.Report
import com.baek.untitledproject.domain.repository.ReportRepository
import com.baek.untitledproject.domain.utils.Result
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val reportRemote: ReportRemote
) : ReportRepository {
    override suspend fun sendReport(report: Report): Result<String> {
        return try {
            val result = reportRemote.sendReport(report)
            Result.Success(result)
        } catch (e: Exception) {
            Log.e("ReportRepository", "신고 실패", e)
            Result.Error("신고하는데 실패하였습니다.", e)
        }
    }
}