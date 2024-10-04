package dev.lutergs.blog.user.domain.repository

import dev.lutergs.blog.user.domain.User
import dev.lutergs.blog.user.domain.entity.Account

interface UserRepository {
    fun getUserByAccount(account: Account): User?
    fun saveUser(user: User): User
}