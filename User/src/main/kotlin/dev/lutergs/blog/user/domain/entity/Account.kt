package dev.lutergs.blog.user.domain.entity

import dev.lutergs.blog.user.domain.value.Email
import java.time.OffsetDateTime

class Account (
    val id: Long,
    val email: Email,
    val createdAt: OffsetDateTime
)