package jp.kaleidot725

import io.ktor.utils.io.streams.asInput
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import jp.kaleidot725.api.WeatherApiClient
import jp.kaleidot725.mcp.WeatherServerFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.buffered

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val client = WeatherApiClient()
    val server = WeatherServerFactory.build(client)
    val transport = StdioServerTransport(System.`in`.asInput(), System.out.asSink().buffered())
    runBlocking {
        val done = Job()
        server.connect(transport)
        server.onClose { done.complete() }
        done.join()
    }
}