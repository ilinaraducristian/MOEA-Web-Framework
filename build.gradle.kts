import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.openapi.generator") version "5.0.1"
    idea
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.spring") version "1.4.30"
}

group = "org.moeawebframework"
version = "3.0"
java.sourceCompatibility = JavaVersion.VERSION_15

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-rsocket")

    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    implementation("io.r2dbc:r2dbc-h2")
    implementation("io.r2dbc:r2dbc-postgresql")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("com.github.kstyrc:embedded-redis:0.6")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.amqp:spring-rabbit-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "15"
    }
}

tasks.openApiGenerate {
    validateSpec.set(false)
    generatorName.set("kotlin-spring")
    outputDir.set("$buildDir/openapi")
    inputSpec.set("$buildDir/api-spec.yml")
    packageName.set("org.moeawebframework")
    apiPackage.set("${packageName.get()}.controllers")
    additionalProperties.put("basePackage", packageName.get())
    modelPackage.set("${packageName.get()}.models")
    additionalProperties.put("apiSuffix", "Controller")
    additionalProperties.put("artifactId", "MOEAWebFramework")
    additionalProperties.put("gradleBuildFile", "false")
    additionalProperties.put("reactive", "true")
    additionalProperties.put("serializationLibrary", "jackson")
    additionalProperties.put("title", "MOEA Web Framework")
}

tasks.register<Exec>("compileSpec") {
    commandLine("python3", "$rootDir/openapi-bundler/main.py", "$rootDir/spec/api.yml", "$buildDir/api-spec.yml")
}

afterEvaluate {
    if (tasks.findByName("compileSpec") != null) {
        tasks.openApiGenerate {
            dependsOn("compileSpec")
        }
    }
}