package jp.kaleidot725.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import jp.kaleidot725.api.response.Alert
import jp.kaleidot725.api.response.Forecast
import jp.kaleidot725.api.response.Points
import kotlinx.serialization.json.Json

class WeatherApiClient {
    private val httpClient = HttpClient {
        defaultRequest {
            url("https://api.weather.gov")
            headers {
                append("Accept", "application/geo+json")
                append("User-Agent", "WeatherApiClient/1.0")
            }
            contentType(ContentType.Application.Json)
        }
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }

    constructor()

    suspend fun getForecast(latitude: Double, longitude: Double): List<String> {
        val points = httpClient.get("/points/$latitude,$longitude").body<Points>()
        val forecast = httpClient.get(points.properties.forecast).body<Forecast>()
        return forecast.properties.periods.map { period ->
            """
            ${period.name}:
            Temperature: ${period.temperature} ${period.temperatureUnit}
            Wind: ${period.windSpeed} ${period.windDirection}
            Forecast: ${period.detailedForecast}
        """.trimIndent()
        }
    }

    suspend fun getAlerts(state: String): List<String> {
        val alerts = httpClient.get("/alerts/active/area/$state").body<Alert>()
        return alerts.features.map { feature ->
            """
            Event: ${feature.properties.event}
            Area: ${feature.properties.areaDesc}
            Severity: ${feature.properties.severity}
            Description: ${feature.properties.description}
            Instruction: ${feature.properties.instruction}
        """.trimIndent()
        }
    }
}