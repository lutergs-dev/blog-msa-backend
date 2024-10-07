package dev.lutergs.blog.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder


@SpringBootApplication(scanBasePackages = ["dev.lutergs.blog"])
class UserApplication

fun main(args: Array<String>) {
    SpringApplicationBuilder(UserApplication::class.java)
        .listeners(ApplicationContextInjector())
        .run(*args)
}
