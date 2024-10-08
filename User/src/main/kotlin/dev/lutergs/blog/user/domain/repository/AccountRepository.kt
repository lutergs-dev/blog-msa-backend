package dev.lutergs.blog.user.domain.repository

import dev.lutergs.blog.user.domain.entity.Account
import dev.lutergs.blog.user.domain.value.Email

interface AccountRepository {
    fun findById(id: Long): Account?
    fun findByEmail(email: Email): Account?
}