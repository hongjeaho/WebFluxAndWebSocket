package com.webflux.message.handler

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.buildAndAwait

@Component
class LoginHandler(
    @Value("\${social.google.token-validation-url}") private val googleTokenValidationUrl: String) {

    companion object {
        private val ALLOWED_EMAIL_LIST = listOf("kansin88@gmail")
    }

    suspend fun login(serverRequest: ServerRequest): ServerResponse {
        val authorizationHandler = serverRequest.headers().header("Authorization")
        if (authorizationHandler.isEmpty()) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).buildAndAwait()
        }

        val token = authorizationHandler[0].substring("Bearer ".length)
        val result = checkedToken(token)

        return if (isAllowedEmail(result["emila"] as String))
            ServerResponse.ok().bodyValue(result).awaitSingle()
        else
            ServerResponse.status(HttpStatus.UNAUTHORIZED).buildAndAwait()
    }

    private fun isAllowedEmail(email: String): Boolean = ALLOWED_EMAIL_LIST.contains(email)

    private suspend fun checkedToken(token: String) =
        WebClient.builder().build()
            .get()
            .uri("$googleTokenValidationUrl=$token")
            .retrieve().bodyToMono(object : ParameterizedTypeReference<Map<String, Any>>() {})
            .awaitSingle()
}
