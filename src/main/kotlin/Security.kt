package com.example

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwk.JwkProviderBuilder
import java.net.URI
import java.util.concurrent.TimeUnit

fun Application.configureSecurity() {
    val issuer = System.getenv("KEYCLOAK_ISSUER") ?: "http://localhost:8080/realms/secure-notes"
    val jwksUrl = System.getenv("KEYCLOAK_JWKS_URL") ?: "$issuer/protocol/openid-connect/certs"
    val jwkProvider = JwkProviderBuilder(URI(jwksUrl).toURL())
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()
    authentication {
        jwt {
            verifier(jwkProvider, issuer)
            validate { credential -> JWTPrincipal(credential.payload) }
        }
    }
}