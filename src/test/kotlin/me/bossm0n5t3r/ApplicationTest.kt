package me.bossm0n5t3r

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.converter
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.deserialize
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.serialization.json.Json
import me.bossm0n5t3r.model.Priority
import me.bossm0n5t3r.model.Task
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() =
        testApplication {
            application {
                module()
            }
            val response = client.get("/")

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("Hello World!", response.bodyAsText())
        }

    @Test
    fun tasksCanBeFoundByPriority() =
        testApplication {
            application {
                module()
            }
            val client =
                createClient {
                    install(ContentNegotiation) {
                        json()
                    }
                }

            val response = client.get("/tasks/byPriority/Medium")
            val results = response.body<List<Task>>()

            assertEquals(HttpStatusCode.OK, response.status)

            val expectedTaskNames = listOf("gardening", "painting")
            val actualTaskNames = results.map(Task::name)
            assertContentEquals(expectedTaskNames, actualTaskNames)
        }

    @Test
    fun invalidPriorityProduces400() =
        testApplication {
            application {
                module()
            }
            val response = client.get("/tasks/byPriority/Invalid")
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }

    @Test
    fun unusedPriorityProduces404() =
        testApplication {
            application {
                module()
            }
            val response = client.get("/tasks/byPriority/Vital")
            assertEquals(HttpStatusCode.NotFound, response.status)
        }

    @Test
    fun newTasksCanBeAdded() =
        testApplication {
            application {
                module()
            }
            val client =
                createClient {
                    install(ContentNegotiation) {
                        json()
                    }
                }

            val task = Task("swimming", "Go to the beach", Priority.Low)
            val response1 =
                client.post("/tasks") {
                    header(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json,
                    )

                    setBody(task)
                }
            assertEquals(HttpStatusCode.Created, response1.status)

            val response2 = client.get("/tasks")
            assertEquals(HttpStatusCode.OK, response2.status)

            val taskNames =
                response2
                    .body<List<Task>>()
                    .map { it.name }

            assertContains(taskNames, "swimming")
        }

    @Test
    fun testWebSocketRoot() =
        testApplication {
            application {
                module()
            }

            val client =
                createClient {
                    install(ContentNegotiation) {
                        json()
                    }
                    install(WebSockets) {
                        contentConverter =
                            KotlinxWebsocketSerializationConverter(Json)
                    }
                }

            val expectedTasks =
                listOf(
                    Task("cleaning", "Clean the house", Priority.Low),
                    Task("gardening", "Mow the lawn", Priority.Medium),
                    Task("shopping", "Buy the groceries", Priority.High),
                    Task("painting", "Paint the fence", Priority.Medium),
                )
            var actualTasks = emptyList<Task>()

            client.webSocket("/websocket/tasks") {
                consumeTasksAsFlow().collect { allTasks ->
                    actualTasks = allTasks
                }
            }

            assertEquals(expectedTasks.size, actualTasks.size)
            expectedTasks.forEachIndexed { index, task ->
                assertEquals(task, actualTasks[index])
            }
        }

    private fun DefaultClientWebSocketSession.consumeTasksAsFlow() =
        incoming
            .consumeAsFlow()
            .map {
                converter?.deserialize<Task>(it) ?: error("Failed to deserialize to Task: $it")
            }.scan(emptyList<Task>()) { list, task ->
                list + task
            }
}
