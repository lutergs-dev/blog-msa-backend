package dev.lutergs.blog.user.infra.token

import dev.lutergs.blog.user.domain.entity.Account
import dev.lutergs.blog.user.domain.repository.TokenGenerator
import dev.lutergs.blog.user.domain.value.AccessToken
import dev.lutergs.blog.user.domain.value.RefreshToken
import dev.lutergs.blog.user.domain.value.Token
import dev.lutergs.blog.user.infra.configuration.properties.TokenConfigurationProperties
import org.springframework.data.redis.core.StringRedisTemplate

class TokenGeneratorImpl(
    private val redisTemplate: StringRedisTemplate,
    private val jwtTokenCreator: JwtTokenCreator,
    private val tokenProperties: TokenConfigurationProperties
): TokenGenerator {

    override fun createTokenFromAccount(account: Account): Pair<AccessToken, RefreshToken> {
        val accessToken = this.jwtTokenCreator
            .createJWSFromAccount(account, tokenProperties.accessTokenExpireHour)
            .let { AccessToken(it) }
        val refreshToken = this.jwtTokenCreator
            .createJWSFromAccount(account, tokenProperties.refreshTokenExpireHour)
            .let { RefreshToken(it) }
        this.redisTemplate.opsForValue().set(account.id.toString(), refreshToken.value)
        return accessToken to refreshToken
    }

    override fun getAccountFromToken(token: Token): Account {
        return if (token is AccessToken) {
            this.jwtTokenCreator.decodeJWS(token.value)
        } else {
            throw IllegalArgumentException("Token type ${token.javaClass.simpleName} is not supported for get account.")
        }
    }
}