package dev.lutergs.blog.user.domain.aggregate

import dev.lutergs.blog.user.domain.entity.Account
import dev.lutergs.blog.user.domain.entity.NickName
import java.time.OffsetDateTime


// root aggregate, account 정보를 가지고 있음
class User internal constructor (
    val id: Long?,
    val accounts: List<Account>,
    val nickName: NickName,
    val createdAt: OffsetDateTime
) {
    fun changeNickName(newNickName: NickName): User {
        return User(this.id, this.accounts, newNickName, this.createdAt)
    }
}