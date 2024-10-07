package dev.lutergs.blog.user.domain

import dev.lutergs.blog.user.domain.entity.Account
import dev.lutergs.blog.user.domain.entity.NickName
import java.time.OffsetDateTime


// root aggregate, account 정보를 가지고 있음
class User internal constructor (
    val account: Account,
    val createdAt: OffsetDateTime,
    val nickName: NickName
) {
    fun changeNickName(newNickName: NickName): User {
        return User(account, createdAt, newNickName)
    }
}