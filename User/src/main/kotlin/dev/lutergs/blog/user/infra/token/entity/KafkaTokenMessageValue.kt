package dev.lutergs.blog.user.infra.token.entity

import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

data class KafkaTokenMessageValue (
    val privateKey: RSAPrivateKey,
    val publicKey: RSAPublicKey
)