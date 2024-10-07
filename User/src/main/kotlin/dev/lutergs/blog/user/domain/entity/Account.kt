package dev.lutergs.blog.user.domain.entity

import dev.lutergs.blog.user.domain.value.Vendor

class Account (
    val id: Long,
    val vendor: Vendor,
    val localPart: String
){
    fun toEmailString(): String {
        return "$localPart@$vendor"
    }

    companion object {
        private val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
        fun fromString(raw: String): Account {
            return if (raw.matches(emailRegex)) {
                raw.split("@").let { Account(Vendor.fromString(it[1]), it[0]) }
            } else {
                throw IllegalArgumentException("Invalid account format : $raw")
            }
        }
    }
}