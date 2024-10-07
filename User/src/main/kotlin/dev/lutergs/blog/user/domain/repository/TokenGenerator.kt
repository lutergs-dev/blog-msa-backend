package dev.lutergs.blog.user.domain.repository

import dev.lutergs.blog.user.domain.entity.Account
import dev.lutergs.blog.user.domain.value.AccessToken
import dev.lutergs.blog.user.domain.value.RefreshToken
import dev.lutergs.blog.user.domain.value.Token

interface TokenGenerator {
    fun createTokenFromAccount(account: Account): Pair<AccessToken, RefreshToken>
    fun getAccountFromToken(token: Token): Account
}