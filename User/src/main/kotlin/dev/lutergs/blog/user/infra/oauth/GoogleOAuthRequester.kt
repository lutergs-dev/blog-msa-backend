package dev.lutergs.blog.user.infra.oauth

import dev.lutergs.blog.user.domain.entity.Account
import dev.lutergs.blog.user.domain.repository.AccountRepository
import dev.lutergs.blog.user.domain.repository.OAuthRequester
import dev.lutergs.blog.user.domain.value.Vendor
import dev.lutergs.blog.user.infra.configuration.properties.GoogleOAuthConfigurationProperties
import dev.lutergs.blog.user.infra.oauth.entity.GoogleOAuthInfo
import dev.lutergs.blog.user.infra.oauth.entity.GoogleUserInfo
import dev.lutergs.blog.utils.nonspring.getVTRestClient
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient

class GoogleOAuthRequester(
    private val properties: GoogleOAuthConfigurationProperties,
    private val accountRepository: AccountRepository
): OAuthRequester {
    private val oauthInfoRequester: RestClient = getVTRestClient(RestClient.builder()
        .baseUrl("https://oauth2.googleapis.com/token")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    )
    private val userInfoRequester: RestClient = getVTRestClient(RestClient.builder()
        .baseUrl("https://www.googleapis.com/oauth2/v1/userinfo")
    )

    override fun getAccountByCode(code: String, redirectionUrl: String): Account {
        return this.getGoogleOauthInfo(code, redirectionUrl)
            .let { this.getGoogleUserInfo(it.accessToken) }
            .email.split("@").first()
            .let { this.accountRepository.createAccountByVendorAndLocalPart(Vendor.GOOGLE, it) }
    }

    private fun getGoogleOauthInfo(code: String, redirectUri: String): GoogleOAuthInfo {
        return this.oauthInfoRequester.post()
            .body(mapOf(
                "code" to code,
                "client_id" to properties.clientId,
                "client_secret" to properties.clientSecret,
                "grant_type" to "authorization_code",
                "redirect_uri" to redirectUri
            ))
            .retrieve()
            .body(GoogleOAuthInfo::class.java)
            ?: throw RuntimeException("GoogleOAuthInfo 에서, 응답이 null 로 왔습니다.")
    }

    private fun getGoogleUserInfo(accessToken: String): GoogleUserInfo {
        return this.userInfoRequester.get()
            .uri { it.queryParam("access_token", accessToken).queryParam("alt", "json").build() }
            .retrieve()
            .body(GoogleUserInfo::class.java)
            ?: throw RuntimeException("GoogleUserInfo 가 null 입니다.")
    }
}