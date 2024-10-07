package dev.lutergs.blog.user

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource

class ApplicationContextInjector: ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    private val logger = KotlinLogging.logger(this::class.simpleName!!)

    override fun onApplicationEvent(event: ApplicationEnvironmentPreparedEvent) {
        // get settings of current profile
        val environment = event.environment
        val envName = environment.getProperty("spring.profiles.active")

        // check if only local or server provided
        if (envName == null) {
            throw IllegalArgumentException("Spring profiles must not be null")
        } else if (envName != "server" && envName != "local") {
            throw IllegalArgumentException("Spring profiles must given between \"server\" and \"local\"")
        }

        // set secure-cookie to true
        environment.propertySources.addLast(this.setCookieSetting(environment))
    }

    private fun setCookieSetting(environment: ConfigurableEnvironment): MapPropertySource {
        val envName = environment.getProperty("spring.profiles.active")
        return mapOf(
            "lutergs.blog.user.token.secure-cookie" to when(envName) {
                "server" -> true
                else -> false
            },
            "lutergs.blog.server.is-https" to when(envName) {
                "server" -> true
                else -> false
            },
            "lutergs.blog.server.server-port" to environment.getProperty("server.port", Long::class.java)
        ).let { MapPropertySource("SecureCookieSetting", it) }
    }
}