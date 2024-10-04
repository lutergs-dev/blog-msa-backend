package dev.lutergs.blog.user.domain.repository

import dev.lutergs.blog.user.domain.entity.Account

interface OAuthRequester {
    fun getAccountByCode(code: String, redirectionUrl: String): Account
}