package dev.lutergs.blog.user.infra.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "lutergs.blog.user.token")
data class TokenConfigurationProperties(
    val expireHour: Int,
    val secureCookie: Boolean,
    val valueKafkaTopicName: String
)