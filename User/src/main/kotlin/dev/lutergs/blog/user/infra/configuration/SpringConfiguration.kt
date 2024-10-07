package dev.lutergs.blog.user.infra.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import dev.lutergs.blog.user.domain.repository.AccountRepository
import dev.lutergs.blog.user.domain.repository.OAuthRequester
import dev.lutergs.blog.user.domain.repository.TokenGenerator
import dev.lutergs.blog.user.domain.repository.UserRepository
import dev.lutergs.blog.user.infra.token.JwtTokenCreator
import dev.lutergs.blog.user.infra.configuration.properties.GoogleOAuthConfigurationProperties
import dev.lutergs.blog.user.infra.configuration.properties.ServerConfigurationProperties
import dev.lutergs.blog.user.infra.configuration.properties.TokenConfigurationProperties
import dev.lutergs.blog.user.infra.controller.UserController
import dev.lutergs.blog.user.infra.controller.route
import dev.lutergs.blog.user.infra.oauth.GoogleOAuthRequester
import dev.lutergs.blog.user.infra.token.TokenGeneratorImpl
import dev.lutergs.blog.user.service.UserService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate

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

    //================================ infra.controller =======================================

    @Bean
    fun httpRoute(userController: UserController) = route(userController)

    @Bean
    fun userController(
        userService: UserService,
        tokenGenerator: TokenGenerator,
        objectMapper: ObjectMapper
    ): UserController = UserController(
        service = userService,
        tokenGenerator = tokenGenerator,
        tokenProperties = this.tokenConfigurationProperties,
        serverProperties = this.serverConfigurationProperties,
        objectMapper = objectMapper
    )


    //================================ infra.oauth ============================================

    @Bean
    fun googleOAuthRequester(
        accountRepository: AccountRepository
    ): GoogleOAuthRequester = GoogleOAuthRequester(
        properties = this.googleOAuthConfigurationProperties,
        accountRepository = accountRepository
    )


    //================================ infra.token ============================================

    @Bean
    fun jwtTokenCreator(
        accountRepository: AccountRepository
    ): JwtTokenCreator = JwtTokenCreator(
        serverProperties = this.serverConfigurationProperties,
        jwtProperties = this.tokenConfigurationProperties.jwt,
        accountRepository = accountRepository
    )

    @Bean
    fun tokenGeneratorImpl(
        redisTemplate: StringRedisTemplate,
        jwtTokenCreator: JwtTokenCreator
    ): TokenGeneratorImpl = TokenGeneratorImpl(
        redisTemplate = redisTemplate,
        jwtTokenCreator = jwtTokenCreator,
        tokenProperties = this.tokenConfigurationProperties
    )


    //================================ service ================================================

    @Bean
    fun userService(
        userRepository: UserRepository,
        oauthRequester: OAuthRequester,
        tokenGenerator: TokenGenerator
    ): UserService = UserService(
        userRepository = userRepository,
        oauthRequester = oauthRequester,
        tokenGenerator = tokenGenerator
    )
}