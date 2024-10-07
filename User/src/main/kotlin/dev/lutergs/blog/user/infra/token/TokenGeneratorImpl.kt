package dev.lutergs.blog.user.infra.token

import dev.lutergs.blog.user.domain.entity.Account
import dev.lutergs.blog.user.domain.repository.TokenGenerator
import dev.lutergs.blog.user.domain.value.Token
import dev.lutergs.blog.user.infra.configuration.properties.TokenConfigurationProperties
import dev.lutergs.blog.user.infra.token.entity.KafkaTokenMessageKey
import dev.lutergs.blog.user.infra.token.entity.KafkaTokenMessageValue
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Jwts
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import java.security.PrivateKey
import java.security.PublicKey
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.properties.Delegates

class TokenGeneratorImpl(
    private val properties: TokenConfigurationProperties
): TokenGenerator {
    private val logger = KotlinLogging.logger(this::class.simpleName!!)
    val topicName = this.properties.valueKafkaTopicName

    data class TokenKeyPair(
        val privateKey: PrivateKey,
        val publicKey: PublicKey,
        val createdAt: OffsetDateTime = OffsetDateTime.now(),
    )

    private var activeKeyPair: TokenKeyPair by Delegates.observable(
        Jwts.SIG.RS512.keyPair().build().let { TokenKeyPair(privateKey = it.private, publicKey = it.public)}
    ) { property, oldValue, newValue ->
        if (Duration.between(newValue.createdAt, oldValue.createdAt).toMinutes() < 180) {
            this.logger.warn { "Active key cannot be rotated in 3 hours! If this is not intended, check for error." }
        }
        this.logger.info { "Active key is rotated!" }
    }
    private var secondKeyPair: TokenKeyPair by Delegates.observable(
        Jwts.SIG.RS512.keyPair().build().let { TokenKeyPair(privateKey = it.private, publicKey = it.public)}
    ) { property, oldValue, newValue ->
        if (Duration.between(newValue.createdAt, oldValue.createdAt).toMinutes() < 180) {
            this.logger.warn { "Second key cannot be rotated in 3 hours! If this is not intended, check for error." }
        }
        this.logger.info { "Second key is set as same as active key!" }
    }

    @KafkaListener(topics = ["#{__listener.topicName}"])
    fun listen(record: ConsumerRecord<KafkaTokenMessageKey, KafkaTokenMessageValue>, acknowledgment: Acknowledgment) {
        if (record.value() == null) {
            this.logger.warn { "Received null privateKey, publickey. Sender info : ${record.key()}" }
        } else {
            this.secondKeyPair = this.activeKeyPair
            this.activeKeyPair = TokenKeyPair(record.value().privateKey, record.value().publicKey)
        }
        acknowledgment.acknowledge()
    }

    override fun createTokenFromAccount(account: Account): Token {
        TODO("Not yet implemented")
    }

    override fun getAccountFromToken(token: Token): Account {
        TODO("Not yet implemented")
    }
}