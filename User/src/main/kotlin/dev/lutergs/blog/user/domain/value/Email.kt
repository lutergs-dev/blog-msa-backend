package dev.lutergs.blog.user.domain.value

data class Email(
    val localPart: String,
    val vendor: Vendor
) {
    companion object {
        private val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
        fun fromString(raw: String): Email {
            return if (raw.matches(emailRegex)) {
                raw.split("@").let { Email(it[1], Vendor.fromString(it[0])) }
            } else {
                throw IllegalArgumentException("Invalid account format : $raw")
            }
        }
    }
}
