package dev.lutergs.blog.utils

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