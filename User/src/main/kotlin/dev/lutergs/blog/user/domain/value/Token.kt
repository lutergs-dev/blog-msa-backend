package dev.lutergs.blog.user.domain.value

abstract class Token(
    val value: String
) {
}

class AccessToken(value: String): Token(value)

class RefreshToken(value: String): Token(value)