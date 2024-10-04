package dev.lutergs.blog.user.domain

import dev.lutergs.blog.user.domain.entity.Account
import java.time.OffsetDateTime


// root aggregate, account 정보를 가지고 있음
class User internal constructor (
    val account: dev.lutergs.blog.user.domain.entity.Account,
    val createdAt: OffsetDateTime,
    val nickName: String
) {
}