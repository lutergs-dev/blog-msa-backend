package dev.lutergs.blog.user.infra.controller.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ChangeNickName (
    @JsonProperty("newNickName")    val newNickName: String
)