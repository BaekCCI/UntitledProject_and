package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.data.ApplicationRequirements
import com.baek.untitledproject.domain.utils.Result

interface ApplyRepository {
    suspend fun getApplicationRequirement(postId:String) : Result<ApplicationRequirements>

    suspend fun submitApplication(postId:String, applicationRequirements: ApplicationRequirements): Result<String>
}