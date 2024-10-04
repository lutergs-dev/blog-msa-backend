package dev.lutergs.blog.user.infra.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "lutergs.blog.user.oauth.google")
data class GoogleOAuthConfigurationProperties(
    val clientId: String,
    val clientSecret: String
) {
}