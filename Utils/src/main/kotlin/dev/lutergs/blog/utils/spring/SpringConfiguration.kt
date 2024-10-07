package dev.lutergs.blog.utils.spring

import com.fasterxml.jackson.databind.ObjectMapper
import dev.lutergs.blog.utils.nonspring.objectMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringConfiguration {

    @ConditionalOnMissingBean
    @Bean
    fun springObjectMapper(): ObjectMapper = objectMapper()
}