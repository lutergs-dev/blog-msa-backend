package dev.lutergs.blog.user.infra.token.entity

import java.time.OffsetDateTime

data class KafkaTokenMessageKey (
    val issuedAt: OffsetDateTime,
    val issuedBy: String
)