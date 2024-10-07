package dev.lutergs.blog.user.infra

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router

private val CONTENT_TYPE = APPLICATION_JSON

fun route() = router {
    accept(CONTENT_TYPE).nest {

    }
}

class UserController(

) {

//    fun signup(request: ServerRequest): ServerResponse {
//        val code = request.params().getFirst("code")
//            ?: return this.createErrorResponse(HttpStatus.BAD_REQUEST,"Google OAuth request malformed!")
//
//    }

    private fun createErrorResponse(code: HttpStatus, value: String): ServerResponse = ServerResponse
        .status(code)
        .contentType(CONTENT_TYPE)
        .body("{ \"error\": \"$value\"}")
}