package dev.lutergs.blog.user.domain.repository

import dev.lutergs.blog.user.domain.aggregate.User
import dev.lutergs.blog.user.domain.entity.Account
import dev.lutergs.blog.user.domain.value.Email

interface UserRepository {
    fun findUserByAccount(account: Account): User?
    fun saveUser(user: User): User
    fun createUserByEmail(email: Email): User
}