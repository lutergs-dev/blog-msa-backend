package dev.lutergs.blog.user.domain.repository

import dev.lutergs.blog.user.domain.value.Email

interface OAuthRequester {
    fun getEmailByCode(code: String, redirectionUrl: String): Email
}