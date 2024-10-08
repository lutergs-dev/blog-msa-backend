package dev.lutergs.blog.user.domain.entity

import java.util.*

class NickName (
    val value: String
){
    init {
        if (!validNicknameRegex.matches(value)) {
            throw IllegalArgumentException("Nickname should be contain only English uppercase, lowercase, numbers.")
        }
        if (this.value.length > 30) {
            throw IllegalArgumentException("Nickname length should be less or equal than 30")
        }
    }

    companion object {
        private val validNicknameRegex = Regex("^[A-Za-z0-9\\-_'.]+$")

        fun createRandomNickName(): NickName {
            return NickName(UUID.randomUUID().toString().replace("-", ""))
        }
    }
}