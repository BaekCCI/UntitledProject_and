package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.data.Report
import com.baek.untitledproject.domain.utils.Result

interface ReportRepository {

    suspend fun sendReport(report: Report): Result<String>

    suspend fun getReportList(userId: String): Result<List<Report>>

}