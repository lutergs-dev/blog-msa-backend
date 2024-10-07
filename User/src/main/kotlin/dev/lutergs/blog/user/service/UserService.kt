package dev.lutergs.blog.user.service

import dev.lutergs.blog.user.domain.User
import dev.lutergs.blog.user.domain.UserFactory
import dev.lutergs.blog.user.domain.entity.Account
import dev.lutergs.blog.user.domain.entity.NickName
import dev.lutergs.blog.user.domain.repository.OAuthRequester
import dev.lutergs.blog.user.domain.repository.TokenGenerator
import dev.lutergs.blog.user.domain.repository.UserRepository
import dev.lutergs.blog.user.domain.value.Token

class UserService(
    private val userRepository: UserRepository,
    private val oAuthRequester: OAuthRequester,
    private val tokenGenerator: TokenGenerator
) {
    fun getUser(token: Token): User? {
        return this.tokenGenerator.getAccountFromToken(token)
            .let { this.userRepository.getUserByAccount(it) }
    }

    fun login(code: String, redirectionUrl: String): Token {
        return this.oAuthRequester.getAccountByCode(code, redirectionUrl)
            .let { this.userRepository.getUserByAccount(it) ?: this.signup(it) }
            .let { this.tokenGenerator.createTokenFromAccount(it.account) }
    }

    fun changeNickName(token: Token, rawNewNickName: String): User {
        val newNickName = NickName(rawNewNickName)
        return this.tokenGenerator.getAccountFromToken(token)
            .let { this.userRepository.getUserByAccount(it) }
            ?.changeNickName(newNickName)
            ?.let { this.userRepository.saveUser(it) }
            ?: throw NoSuchElementException("No user found with provided token. Token : $token")
    }

    private fun signup(account: Account): User {
        return UserFactory.createUserFromAccount(account)
            .let { this.userRepository.saveUser(it) }
    }
}