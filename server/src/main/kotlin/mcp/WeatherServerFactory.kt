package jp.kaleidot725.mcp

import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import jp.kaleidot725.api.WeatherApiClient
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

object WeatherServerFactory {
    fun build(apiClient: WeatherApiClient) = Server(
        Implementation(name = "weather", version = "1.0.0"),
        ServerOptions(capabilities = ServerCapabilities(tools = ServerCapabilities.Tools(listChanged = true)))
    ).apply {
        addAlertsTool(apiClient)
        addForecastTool(apiClient)
    }

    private fun Server.addAlertsTool(apiClient: WeatherApiClient) {
        addTool(
            name = "get_alerts",
            description = """
            Get weather alerts for a US state. Input is Two-letter US state code (e.g. CAY, NY)
        """.trimIndent(),
            inputSchema = Tool.Input(
                properties = buildJsonObject {
                    putJsonObject("state") {
                        put("type", "string")
                        put("description", "Two-letter US state code (e.g. CA, NY)")
                    }
                },
                required = listOf("state")
            )
        ) { request ->
            val state = request.arguments["state"]?.jsonPrimitive?.content
            if (state == null) {
                return@addTool CallToolResult(
                    content = listOf(
                        TextContent("The 'state' parameter is required")
                    )
                )
            }

            val alerts = apiClient.getAlerts(state)
            CallToolResult(content = alerts.map { TextContent(it) })
        }
    }

    private fun Server.addForecastTool(apiClient: WeatherApiClient) {
        addTool(
            name = "get_forecast",
            description = """
            Get weather forecast for a specific latitude/longitude
        """.trimIndent(),
            inputSchema = Tool.Input(
                properties = buildJsonObject {
                    putJsonObject("latitude") { put("type", "number") }
                    putJsonObject("longitude") { put("type", "number") }
                },
                required = listOf("latitude", "longitude")
            )
        ) { request ->
            val latitude = request.arguments["latitude"]?.jsonPrimitive?.doubleOrNull
            val longitude = request.arguments["longitude"]?.jsonPrimitive?.doubleOrNull
            if (latitude == null || longitude == null) {
                return@addTool CallToolResult(
                    content = listOf(TextContent("The 'latitude' and 'longitude' parameters are required."))
                )
            }

            val forecast = apiClient.getForecast(latitude, longitude)

            CallToolResult(content = forecast.map { TextContent(it) })
        }
    }
}