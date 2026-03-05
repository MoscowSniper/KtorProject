buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
        classpath("io.ktor:ktor-gradle-plugin:2.3.8")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.22")
    }
}

apply(plugin = "org.jetbrains.kotlin.jvm")
apply(plugin = "application")
apply(plugin = "io.ktor.plugin")
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
}

configure<io.ktor.plugin.features.KtorExtension> {
    fatJar {
        archiveFileName.set("app.jar")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("io.ktor:ktor-server-core-jvm:2.3.8")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.8")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.8")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.8")
    implementation("io.ktor:ktor-server-auth-jvm:2.3.8")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.3.8")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.3.8")
    implementation("io.ktor:ktor-server-status-pages-jvm:2.3.8")
    implementation("io.ktor:ktor-server-swagger-jvm:2.3.8")

    implementation("org.jetbrains.exposed:exposed-core:0.47.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.47.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.47.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.47.0")
    implementation("org.postgresql:postgresql:42.7.1")
    implementation("com.zaxxer:HikariCP:5.1.0")

    implementation("redis.clients:jedis:5.1.0")
    implementation("com.rabbitmq:amqp-client:5.20.0")

    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    testImplementation("io.ktor:ktor-server-test-host-jvm:2.3.8")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.22")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
    testImplementation("org.testcontainers:postgresql:1.19.3")
    testImplementation("org.testcontainers:rabbitmq:1.19.3")
    testImplementation("org.testcontainers:testcontainers:1.19.3")
}

tasks.test {
    useJUnitPlatform()
}
