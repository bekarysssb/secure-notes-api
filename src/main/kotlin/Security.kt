package com.example

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwk.JwkProviderBuilder
import java.net.URL
import java.util.concurrent.TimeUnit

fun Application.configureSecurity() {
    val keycloakRealmUrl = "http://localhost:8080/realms/secure-notes"
    val jwkProvider = JwkProviderBuilder(URL("$keycloakRealmUrl/protocol/openid-connect/certs"))
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    authentication {
        jwt {
            verifier(jwkProvider, keycloakRealmUrl)
            validate { credential -> JWTPrincipal(credential.payload) }
        }
    }
}