package dev.lutergs.blog.user.infra.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties(prefix = "lutergs.blog.user.token")
data class TokenConfigurationProperties(
    val secureCookie: Boolean,
    val accessTokenExpireHour: Int,
    val refreshTokenExpireHour: Int,
    @NestedConfigurationProperty val jwt: JWTConfigurationProperties
)

data class JWTConfigurationProperties(
    val rsaKeyReceiveTopicName: String,
    val issuer: String,
    val subject: String,
    val claimSuffix: String,
)