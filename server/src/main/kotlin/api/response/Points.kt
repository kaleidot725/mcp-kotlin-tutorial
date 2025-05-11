package jp.kaleidot725.api.response

import kotlinx.serialization.Serializable

@Serializable
data class Points(
    val properties: Properties
) {
    @Serializable
    data class Properties(val forecast: String)
}