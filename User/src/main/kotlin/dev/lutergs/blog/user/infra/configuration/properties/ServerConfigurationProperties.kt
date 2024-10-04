package dev.lutergs.blog.user.infra.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "lutergs.blog.user.server")
data class ServerConfigurationProperties(
    val backend: String,
    val frontend: String,
    val rootDomain: String
)