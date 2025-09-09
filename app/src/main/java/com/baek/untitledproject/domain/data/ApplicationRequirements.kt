package com.baek.untitledproject.domain.data

data class ApplicationRequirements(
    val postId:String = "",
    val postTitle:String = "",
    val postOrganization :String = "",
    val postAuthorUserId:String = "",

    val requiresName: Boolean = false,
    val requiresStudentId: Boolean = false,
    val requiresDepartment: Boolean = false,
    val requiresGender: Boolean = false,
    val requiresAge: Boolean = false,
    //val requiresPhone: Boolean = false,
    val customQuestions: List<CustomQuestion> = emptyList()
)
