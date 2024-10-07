package dev.lutergs.blog.user.infra.token.serialize

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import dev.lutergs.blog.user.infra.token.entity.KafkaTokenMessageKey
import org.apache.kafka.common.serialization.Deserializer

class KafkaTokenMessageKeyDeserializer: Deserializer<KafkaTokenMessageKey> {
    private val objectMapper = ObjectMapper()
        .registerKotlinModule()
        .registerModule(JavaTimeModule())

    override fun deserialize(topic: String?, data: ByteArray?): KafkaTokenMessageKey {
        return objectMapper.readValue(data, KafkaTokenMessageKey::class.java)
    }
}