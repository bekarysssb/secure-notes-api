package com.example

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import java.util.concurrent.ConcurrentHashMap
import java.util.UUID

@Serializable
data class NoteRequest(val content: String)

@Serializable
data class NoteResponse(val id: String, val content: String)

data class StoredNote(val ownerId: String, val encryptedContent: String)

val notesDb = ConcurrentHashMap<String, StoredNote>()

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("SecureNotes API is running")
        }

        authenticate {
            post("/notes") {
                val principal = call.principal<JWTPrincipal>()!!
                val ownerId = principal.payload.subject
                val req = call.receive<NoteRequest>()
                val id = UUID.randomUUID().toString()
                val encrypted = NoteCrypto.encrypt(req.content)
                notesDb[id] = StoredNote(ownerId, encrypted)
                call.respond(HttpStatusCode.Created, NoteResponse(id, req.content))
            }

            get("/notes/{id}") {
                val principal = call.principal<JWTPrincipal>()!!
                val ownerId = principal.payload.subject
                val id = call.parameters["id"]!!
                val note = notesDb[id]
                if (note == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                if (note.ownerId != ownerId) {
                    call.respond(HttpStatusCode.Forbidden) // IDOR fix
                    return@get
                }
                call.respond(NoteResponse(id, NoteCrypto.decrypt(note.encryptedContent)))
            }
        }
    }
}