package dev.lutergs.blog.user.domain.value

enum class Vendor {
    GOOGLE;

    fun getDomain(): String = enumToStringMap[this]!!

    companion object {
        private val enumToStringMap = mapOf(
            GOOGLE to "gmail.com"
        )
        private val stringToEnumMap = mapOf(
            "gmail.com" to GOOGLE
        )
        fun fromString(string: String): Vendor {
            return stringToEnumMap[string] ?: throw IllegalArgumentException("Unknown Vendor '$string'")
        }
    }
}