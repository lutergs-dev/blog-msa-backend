package dev.lutergs.blog.user.infra.configuration

import dev.lutergs.blog.user.infra.configuration.properties.GoogleOAuthConfigurationProperties
import dev.lutergs.blog.user.infra.configuration.properties.ServerConfigurationProperties
import dev.lutergs.blog.user.infra.configuration.properties.TokenConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(value = [
    GoogleOAuthConfigurationProperties::class,
    ServerConfigurationProperties::class,
    TokenConfigurationProperties::class
])
class SpringConfiguration(
    private val googleOAuthConfigurationProperties: GoogleOAuthConfigurationProperties,
    private val serverConfigurationProperties: ServerConfigurationProperties,
    private val tokenConfigurationProperties: TokenConfigurationProperties
) {
}