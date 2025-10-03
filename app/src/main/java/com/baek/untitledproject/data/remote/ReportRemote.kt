package com.baek.untitledproject.data.remote

import com.baek.untitledproject.data.model.ReportResponse
import com.baek.untitledproject.data.model.mapper.toDomain
import com.baek.untitledproject.data.model.mapper.toResponse
import com.baek.untitledproject.domain.data.Report
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class ReportRemote @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val reports = db.collection("reports")

    suspend fun sendReport(report: Report): String = coroutineScope {

        val now = LocalDate.now()
        val doc = reports.document()
        val id = doc.id

        val response = report.toResponse(id, now)

        doc.set(response).await()

        id
    }

    suspend fun getReportList(userId: String): List<Report> {

        val snap = reports.whereEqualTo("reporter_user_id", userId)
            .get().await()

        val docs = snap.documents.sortedByDescending { it.getTimestamp("created_at") }

        return docs.mapNotNull { doc ->
            doc.toObject(ReportResponse::class.java)?.toDomain()
        }
    }

}