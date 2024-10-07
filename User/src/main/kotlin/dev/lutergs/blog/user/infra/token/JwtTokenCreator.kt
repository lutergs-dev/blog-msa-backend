package dev.lutergs.blog.user.infra.token

import dev.lutergs.blog.user.domain.entity.Account
import dev.lutergs.blog.user.domain.repository.AccountRepository
import dev.lutergs.blog.user.infra.configuration.properties.JWTConfigurationProperties
import dev.lutergs.blog.user.infra.configuration.properties.ServerConfigurationProperties
import dev.lutergs.blog.user.infra.token.entity.KafkaTokenMessageKey
import dev.lutergs.blog.user.infra.token.entity.KafkaTokenMessageValue
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.IncorrectClaimException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MissingClaimException
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import java.security.PrivateKey
import java.security.PublicKey
import java.time.Duration
import java.time.OffsetDateTime
import java.util.Date
import kotlin.properties.Delegates

class JwtTokenCreator(
    private val serverProperties: ServerConfigurationProperties,
    private val jwtProperties: JWTConfigurationProperties,
    private val accountRepository: AccountRepository
) {
    private val logger = KotlinLogging.logger(this::class.simpleName!!)
    val topicName = this.jwtProperties.rsaKeyReceiveTopicName

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

    fun createJWSFromAccount(account: Account, expireHour: Int): String {
        val currentDate = OffsetDateTime.now()
        val expireDate = currentDate.plusHours(expireHour.toLong())
        return Jwts.builder()
            .issuer(this.jwtProperties.issuer)
            .subject(this.jwtProperties.subject)
            .expiration(Date.from(expireDate.toInstant()))
            .notBefore(Date.from(currentDate.toInstant()))
            .issuedAt(Date.from(currentDate.toInstant()))
            .claim("${this.serverProperties.url}${this.jwtProperties.claimSuffix}", account.id)
            .encryptWith(this.activeKeyPair.publicKey, Jwts.KEY.RSA_OAEP_256, Jwts.ENC.A256CBC_HS512)
            .compact()
    }

    fun decodeJWS(value: String): Account {
        return runCatching {
            Jwts.parser()
                .requireIssuer(this.jwtProperties.issuer)
                .requireSubject(this.jwtProperties.subject)
                .decryptWith(this.activeKeyPair.privateKey)
                .build().parseEncryptedClaims(value)
        }.recoverCatching {
            when (it) {
                is ExpiredJwtException -> {
                    this.logger.debug { "JWS is expired. User must re-login. Value: $value" }
                    throw it
                }
                is MissingClaimException, is IncorrectClaimException -> {
                    this.logger.error { "JWS is decoded, but requirement check failed! POSSIBLE KEY BROKEN STATUS!!!" }
                    throw it
                }
                else -> run {
                    this.logger.warn { "Failed to decrypt token with using first key pair. Using second key pair..." }
                    Jwts.parser()
                        .requireIssuer(this.jwtProperties.issuer)
                        .requireSubject(this.jwtProperties.subject)
                        .decryptWith(this.secondKeyPair.privateKey)
                        .build().parseEncryptedClaims(value)
                }
            }
        }.mapCatching { jweClaims ->
            val id = jweClaims.payload["${this.serverProperties.url}${this.jwtProperties.claimSuffix}"]
                ?.let { it as Long }
                ?: throw IllegalStateException("JWT does not have account info, or claim value is not String type. value : $value")
            this.accountRepository.findById(id) ?: throw IllegalArgumentException("No account found with id $id")
        }.getOrThrow()
    }
}