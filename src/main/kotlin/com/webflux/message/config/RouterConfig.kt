package com.webflux.message.config

import com.webflux.message.handler.LoginHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouterConfig(private val loginHandler: LoginHandler) {

    @Bean
    fun loginRouters() = coRouter {
        "/api".nest {
            GET("/login", loginHandler::login)
        }
    }
}
