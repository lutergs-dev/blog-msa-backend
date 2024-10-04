package dev.lutergs.blog.user.infra.oauth.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleUserInfo(
    @JsonProperty("id")             val id: String,
    @JsonProperty("email")          val email: String,
    @JsonProperty("verified_email") val isVerified: Boolean,
    @JsonProperty("picture")        val pictureUrl: String
)