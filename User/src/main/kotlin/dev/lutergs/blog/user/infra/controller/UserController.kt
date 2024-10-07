package dev.lutergs.blog.user.infra.controller

import com.fasterxml.jackson.databind.ObjectMapper
import dev.lutergs.blog.user.domain.repository.TokenGenerator
import dev.lutergs.blog.user.domain.value.AccessToken
import dev.lutergs.blog.user.domain.value.RefreshToken
import dev.lutergs.blog.user.infra.configuration.properties.ServerConfigurationProperties
import dev.lutergs.blog.user.infra.configuration.properties.TokenConfigurationProperties
import dev.lutergs.blog.user.infra.controller.dto.ChangeNickName
import dev.lutergs.blog.user.service.UserService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.http.Cookie
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router
import java.net.URI

private val CONTENT_TYPE = APPLICATION_JSON

fun route(userController: UserController) = router {
    accept(CONTENT_TYPE).nest {
        GET("/api/users", userController::getUser)
        PATCH("/api/users", userController::changeNickName)
        POST("/api/users/refresh", userController::refreshToken)
        GET("/api/users/signup", userController::signup)
        POST("/api/users/logout", userController::logout)
    }
}

class UserController(
    private val service: UserService,
    private val tokenGenerator: TokenGenerator,
    private val tokenProperties: TokenConfigurationProperties,
    private val serverProperties: ServerConfigurationProperties,
    private val objectMapper: ObjectMapper
) {
    private val cookieName: String = this.tokenProperties.jwt.issuer

    fun getUser(request: ServerRequest): ServerResponse {
        val accessToken = request.headers().firstHeader("access-token")?.let { AccessToken(it) }
            ?: return this.createErrorResponse(HttpStatus.UNAUTHORIZED, "Access token is missing")
        return runCatching {
            this.service.getUser(accessToken)
                ?.let { ServerResponse.ok().contentType(CONTENT_TYPE).body(it) }
                ?: this.createErrorResponse(HttpStatus.NOT_FOUND, "User not found")
        }.recoverCatching { when (it) {
            is ExpiredJwtException -> this.createErrorResponse(HttpStatus.UNAUTHORIZED, "Access token is expired")
            is NotFoundException -> this.createErrorResponse(HttpStatus.NOT_FOUND, "Not found")
            else -> this.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
        } }.getOrThrow()
    }

    fun changeNickName(request: ServerRequest): ServerResponse {
        val accessToken = request.headers().firstHeader("access-token")?.let { AccessToken(it) }
            ?: return this.createErrorResponse(HttpStatus.UNAUTHORIZED, "Access token is missing")
        val rawNickName = request.body(ChangeNickName::class.java)
        return runCatching {
            this.service.changeNickName(accessToken, rawNickName.newNickName)
                .let { ServerResponse.ok().contentType(CONTENT_TYPE).body(it) }
        }.recoverCatching { when (it) {
            is ExpiredJwtException -> this.createErrorResponse(HttpStatus.UNAUTHORIZED, "Access token is expired")
            is JwtException -> this.createErrorResponse(HttpStatus.BAD_REQUEST, "invalid JWT format Access Token")
            is NoSuchElementException -> this.createErrorResponse(HttpStatus.NOT_FOUND, it.localizedMessage)
            else -> this.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
        } }.getOrThrow()
    }

    fun refreshToken(request: ServerRequest): ServerResponse {
        val refreshToken = this.getCookie(request)
            ?.let { RefreshToken(it) }
            ?: return createErrorResponse(HttpStatus.NOT_FOUND, "Refresh token not found")
        return runCatching {
            this.tokenGenerator.getAccountFromToken(refreshToken)
                .let { this.tokenGenerator.createTokenFromAccount(it) }
                .let { ServerResponse.ok().contentType(CONTENT_TYPE)
                    .cookie(this.createCookie(it.first, it.second, false))
                    .body(mapOf("accessToken" to it.first))
                }
        }.recoverCatching { when (it) {
            is JwtException -> this.createErrorResponse(HttpStatus.BAD_REQUEST, "invalid JWT format Access Token")
            is NoSuchElementException -> this.createErrorResponse(HttpStatus.NOT_FOUND, it.localizedMessage)
            else -> this.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
        } }.getOrThrow()
    }

    fun signup(request: ServerRequest): ServerResponse {
        val code = request.params().getFirst("code")
            ?: return this.createErrorResponse(HttpStatus.BAD_REQUEST,"Google OAuth request malformed!")
        return runCatching {
            this.service.login(code, "${this.serverProperties.url}/user/signup")
                .let { ServerResponse.permanentRedirect(URI.create("${this.serverProperties.url}/user"))
                    .cookie(this.createCookie(it.first, it.second, false))
                    .body(mapOf("accessToken" to it.first))
                }
        }.recoverCatching { when (it) {
            is JwtException -> this.createErrorResponse(HttpStatus.BAD_REQUEST, "invalid JWT format Access Token")
            is NoSuchElementException -> this.createErrorResponse(HttpStatus.NOT_FOUND, it.localizedMessage)
            else -> this.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error")
        } }.getOrThrow()
    }

    fun logout(request: ServerRequest): ServerResponse {
        return ServerResponse.ok()
            .contentType(CONTENT_TYPE)
            .cookie(this.createCookie(AccessToken("null"), RefreshToken("null"), true))
            .build()
    }

    private fun getCookie(request: ServerRequest): String? {
        return request.cookies()[this.cookieName]
            ?.find { it.name == this.cookieName }
            ?.value
    }

    private fun createCookie(accessToken: AccessToken, refreshToken: RefreshToken, isLogout: Boolean): Cookie {
        return Cookie(this.cookieName, this.objectMapper.writeValueAsString(mapOf(
            "accessToken" to accessToken, "refreshToken" to refreshToken
        ))).also {
            it.isHttpOnly = this.tokenProperties.secureCookie
            it.domain = this.serverProperties.url
            it.maxAge = when(isLogout) {
                false -> this.tokenProperties.refreshTokenExpireHour * 3600
                true -> 0
            }
            it.setAttribute("SameSite", "Strict")
        }
    }

    private fun createErrorResponse(code: HttpStatus, value: String): ServerResponse = ServerResponse
        .status(code)
        .contentType(CONTENT_TYPE)
        .body("{ \"error\": \"$value\"}")
}