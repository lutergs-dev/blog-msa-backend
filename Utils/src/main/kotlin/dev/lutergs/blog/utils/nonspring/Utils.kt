package dev.lutergs.blog.utils.nonspring

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.net.http.HttpClient
import java.util.concurrent.Executors


fun getVTRestClient(builder: RestClient.Builder): RestClient {
    return builder
        .requestFactory(
            JdkClientHttpRequestFactory(
            HttpClient.newBuilder()
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .build()
            )
        ).build()
}

fun objectMapper(): ObjectMapper = ObjectMapper()
    .registerKotlinModule()
    .registerModule(JavaTimeModule())