package dev.lutergs.blog.user.service

import dev.lutergs.blog.user.domain.aggregate.User
import dev.lutergs.blog.user.domain.entity.NickName
import dev.lutergs.blog.user.domain.repository.AccountRepository
import dev.lutergs.blog.user.domain.repository.OAuthRequester
import dev.lutergs.blog.user.domain.repository.TokenGenerator
import dev.lutergs.blog.user.domain.repository.UserRepository
import dev.lutergs.blog.user.domain.value.AccessToken
import dev.lutergs.blog.user.domain.value.RefreshToken
import dev.lutergs.blog.user.domain.value.Token
import jakarta.transaction.Transactional

open class UserService(
    private val userRepository: UserRepository,
    private val accountRepository: AccountRepository,
    private val oauthRequester: OAuthRequester,
    private val tokenGenerator: TokenGenerator,
) {
    @Transactional
    open fun getUser(token: Token): User? {
        return this.tokenGenerator.getAccountFromToken(token)
            .let { this.userRepository.findUserByAccount(it) }
    }

    @Transactional
    open fun login(code: String, redirectionUrl: String): Pair<AccessToken, RefreshToken> {
        return this.oauthRequester.getEmailByCode(code, redirectionUrl)
            .let { email ->
                this.accountRepository.findByEmail(email)
                    ?.let { this.userRepository.findUserByAccount(it) }
                    ?: this.userRepository.createUserByEmail(email)
            }.let { this.tokenGenerator.createTokenFromAccount(it.accounts.first()) }
    }

    @Transactional
    open fun changeNickName(token: AccessToken, rawNewNickName: String): User {
        val newNickName = NickName(rawNewNickName)
        return this.tokenGenerator.getAccountFromToken(token)
            .let { this.userRepository.findUserByAccount(it) }
            ?.changeNickName(newNickName)
            ?.let { this.userRepository.saveUser(it) }
            ?: throw NoSuchElementException("No user found with provided token. Token : $token")
    }
}