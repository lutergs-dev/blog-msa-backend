package dev.lutergs.blog.user.infra.token.serialize

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import dev.lutergs.blog.user.infra.token.entity.KafkaTokenMessageValue
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.common.serialization.Deserializer
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

class KafkaTokenMessageValueDeserializer: Deserializer<KafkaTokenMessageValue?> {
    private val objectMapper = ObjectMapper().registerKotlinModule()
    private val logger = KotlinLogging.logger(this::class.simpleName!!)

    data class RawValue(
        val privateKey: String,
        val publicKey: String
    )

    override fun deserialize(topic: String?, data: ByteArray?): KafkaTokenMessageValue? {
        return runCatching {
            val rawValue = this.objectMapper.readValue(data, RawValue::class.java)
            KafkaTokenMessageValue(
                privateKey = this.loadPrivateKey(rawValue.privateKey),
                publicKey = this.loadPublicKey(rawValue.publicKey)
            )
        }.onFailure { this.logger.error(it) { "decode Private Key, Public Key Failed! Key will not update."} }
            .getOrNull()

    }

    private fun loadPrivateKey(rawPrivateKey: String): RSAPrivateKey {
        val keyPem = rawPrivateKey
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s+".toRegex(), "")
        val decoded = Base64.getDecoder().decode(keyPem)
        val keySpec = PKCS8EncodedKeySpec(decoded)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec) as RSAPrivateKey
    }

    private fun loadPublicKey(rawPublicKey: String): RSAPublicKey {
        val keyPem = rawPublicKey
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s+".toRegex(), "")
        val decoded = Base64.getDecoder().decode(keyPem)
        val keySpec = X509EncodedKeySpec(decoded)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec) as RSAPublicKey
    }
}