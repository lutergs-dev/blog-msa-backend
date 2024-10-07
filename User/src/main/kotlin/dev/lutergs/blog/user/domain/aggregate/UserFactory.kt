package dev.lutergs.blog.user.domain.aggregate

import dev.lutergs.blog.user.domain.entity.Account
import dev.lutergs.blog.user.domain.entity.NickName
import java.time.OffsetDateTime

object UserFactory {
    fun createUserFromAccount(account: Account): User {
        return User(account, OffsetDateTime.now(), NickName.createRandomNickName())
    }
}