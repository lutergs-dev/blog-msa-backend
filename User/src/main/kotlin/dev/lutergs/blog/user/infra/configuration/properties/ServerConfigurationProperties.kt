package dev.lutergs.blog.user.infra.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "lutergs.blog.server")
data class ServerConfigurationProperties(
    val rootDomain: String,
    val serverPort: Int,
    val isHttps: Boolean
) {
    val url: String = run {
        when (isHttps) {
            true -> "https://${rootDomain}"
            false -> "https://${rootDomain}:${serverPort}"
        }
    }
}