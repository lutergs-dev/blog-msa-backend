package dev.lutergs.blog.user.domain.value

enum class Vendor {
    GOOGLE {
        override fun getDomain(): String = "gmail.com"
    };

    abstract fun getDomain(): String
}