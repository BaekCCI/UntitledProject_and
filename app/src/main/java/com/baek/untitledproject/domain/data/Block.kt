package com.baek.untitledproject.domain.data

import java.time.LocalDate

data class Block(
    val blockId: String,
    val blockedUserId: String,
    val blockedUserName:String,
    val createdAt: LocalDate?
)