# ktor-sample

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:
 * [Ktor Documentation](https://ktor.io/docs/home.html)
 * [Ktor GitHub page](https://github.com/ktorio/ktor)
 * [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). [Request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up).


## Features
Here's a list of features included in this project:

| Name | Description |
|------|-------------|
| [Content Negotiation](https://start.ktor.io/p/io.ktor/server-content-negotiation) | Provides automatic content conversion according to Content-Type and Accept headers |
| [kotlinx.serialization](https://start.ktor.io/p/io.ktor/server-kotlinx-serialization) | Handles JSON serialization using kotlinx.serialization library |
| [Authentication](https://start.ktor.io/p/io.ktor/server-auth) | Provides extension point for handling the Authorization header |
| [Authentication JWT](https://start.ktor.io/p/io.ktor/server-auth-jwt) | Handles JSON Web Token (JWT) bearer authentication scheme |


## Building & Running
To build or run the project, use one of the following tasks:


| Task | Description |
|------|-------------|
| `./gradlew test`    | Run the tests     |
| `./gradlew build`   | Build the project |
| `./gradlew run`     | Run the server    |

If the server starts successfully, you'll see the following output:
```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

# SecureNotes API

A minimal REST API for storing encrypted notes, built for the SSSD course midterm project.

## Stack
- Kotlin + Ktor (Netty engine)
- Keycloak for OAuth2/OIDC authentication
- AES-GCM for encrypting note content at rest
- bcrypt-ready password hashing (delegated to Keycloak)

## Endpoints
- `POST /notes` — create an encrypted note (requires JWT)
- `GET /notes/{id}` — fetch a single note, owner-only (requires JWT)
- `GET /notes` — list all notes belonging to the authenticated user (requires JWT)

## Running locally
1. Start Keycloak: `docker run -d -p 8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin --name keycloak quay.io/keycloak/keycloak:latest start-dev`
2. Create a realm `secure-notes`, a confidential client `secure-notes-api` with Direct Access Grants enabled, and at least one user.
3. Run the Ktor app (listens on port 8081).
4. Get a token via the Keycloak token endpoint and call the API with `Authorization: Bearer <token>`.

## Security notes
- Note content is encrypted with AES-GCM before storage — never stored in plaintext.
- The AES key should be supplied via the `AES_KEY` environment variable in production (falls back to a random key per run in dev mode).
- Access control is enforced on `GET /notes/{id}` — only the note's owner can read it.