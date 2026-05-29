plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "rolfrander"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    
    // include for JVM target
    val kotlinxHtmlVersion = "0.12.0"
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinxHtmlVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation(platform("org.junit:junit-bom:6.1.0"))
	testImplementation("org.junit.jupiter:junit-jupiter")
    //testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
	testLogging {
		events("passed", "skipped", "failed")
	}
}

/**
 * This will redirect compilation-errors to a separate file which can be 
 * read by vim for quickfix.  The file is truncated on each build.
 */
abstract class KotlinErrorLogger : BuildService<BuildServiceParameters.None>, AutoCloseable {

    private val file = File("build/kotlin-errors.log")

    init {
        file.parentFile.mkdirs()
        file.writeText("") // truncate at build start
    }

    fun log(line: String) {
        if (line.startsWith("e:") || line.startsWith("w:")) {
            file.appendText(line + "\n")
        }
    }

    override fun close() {}
}

val loggerService = gradle.sharedServices.registerIfAbsent(
    "kotlinErrorLogger",
    KotlinErrorLogger::class.java
) {}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    val service = loggerService.get()

    logging.addStandardErrorListener { message ->
        service.log(message?.toString() ?: "(nomessage)")
    }
}
/******************** End of logging customization ********************/
