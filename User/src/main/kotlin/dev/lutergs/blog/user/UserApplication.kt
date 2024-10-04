package dev.lutergs.blog.user

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication(scanBasePackages = ["dev.lutergs.blog"])
class UserApplication

fun main(args: Array<String>) {
    SpringApplication.run(dev.lutergs.blog.user.UserApplication::class.java)
}
