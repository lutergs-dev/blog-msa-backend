package dev.lutergs.blog.user.domain.entity

import java.util.*

class NickName (
    val value: String
){
    init {
        if (!validNicknameRegex.matches(value)) {
            throw IllegalArgumentException("Nickname should be contain only English uppercase, lowercase, numbers.")
        }
    }

    companion object {
        private val validNicknameRegex = Regex("^[A-Za-z0-9\\-_'\\.]+$")

        fun createRandomNickName(): NickName {
            return NickName(UUID.randomUUID().toString().replace("-", ""))
        }
    }
}