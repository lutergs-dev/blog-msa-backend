package dev.lutergs.blog.user.domain.aggregate

import dev.lutergs.blog.user.domain.entity.Account
import dev.lutergs.blog.user.domain.entity.NickName
import java.time.OffsetDateTime


// root aggregate, account 정보를 가지고 있음
class User internal constructor (
    val id: Long?,
    val account: Account,
    val createdAt: OffsetDateTime,
    val nickName: NickName
) {
    fun changeNickName(newNickName: NickName): User {
        return User(id, account, createdAt, newNickName)
    }
}