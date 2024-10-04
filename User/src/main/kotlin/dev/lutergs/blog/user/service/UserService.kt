package dev.lutergs.blog.user.service

import dev.lutergs.blog.user.domain.repository.OAuthRequester
import dev.lutergs.blog.user.domain.repository.UserRepository

class UserService(
    private val userRepository: UserRepository,
    private val oAuthRequester: OAuthRequester
) {

    fun signup(code: String, redirectionUrl: String): String {
        this.oAuthRequester.getAccountByCode(code, redirectionUrl)
            .let { this.userRepository.getUserByAccount(it) }
    }
}