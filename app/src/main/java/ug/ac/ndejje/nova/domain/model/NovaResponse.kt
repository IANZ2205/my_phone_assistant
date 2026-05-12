package ug.ac.ndejje.nova.domain.model

data class NovaResponse(
    val response: String,
    val status: String? = null,
    val version: String? = null
)
