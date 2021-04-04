import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    id("org.springframework.boot") version "2.4.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.openapi.generator") version "5.0.1"
    idea
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.spring") version "1.4.30"
}

group = "org.moeawebframework"
version = "4.0.1"
java.sourceCompatibility = JavaVersion.VERSION_15

repositories {
    mavenCentral()
}

extra["testcontainersVersion"] = "1.15.2"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-rsocket")

    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.minio:minio:8.1.0")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    implementation("dev.miku:r2dbc-mysql")
    runtimeOnly("mysql:mysql-connector-java")
    implementation("io.r2dbc:r2dbc-h2")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.amqp:spring-rabbit-test")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs =
        listOf("--add-opens", "java.base/jdk.internal.misc=ALL-UNNAMED", "-Dio.netty.tryReflectionSetAccessible=true")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "15"
    }
}

tasks.withType<BootRun> {
    jvmArgs =
        listOf("--add-opens", "java.base/jdk.internal.misc=ALL-UNNAMED", "-Dio.netty.tryReflectionSetAccessible=true")
}

tasks.openApiGenerate {
    validateSpec.set(false)
    generatorName.set("kotlin-spring")
    outputDir.set("$buildDir/openapi")
    inputSpec.set("$rootDir/spec/spec.yml")
    packageName.set("org.moeawebframework")
    apiPackage.set("${packageName.get()}.controllers")
    modelPackage.set("${packageName.get()}.models")
    templateDir.set("$rootDir/templates")
    additionalProperties.put("basePackage", packageName.get())
    additionalProperties.put("apiSuffix", "Controller")
    additionalProperties.put("artifactId", "MOEAWebFramework")
    additionalProperties.put("gradleBuildFile", "false")
    additionalProperties.put("reactive", "true")
    additionalProperties.put("serializationLibrary", "jackson")
    additionalProperties.put("title", "MOEA Web Framework")
}

tasks.register<Exec>("compileSpec") {
    commandLine("python3", "$rootDir/openapi-bundler/main.py", "$rootDir/spec/api.yml", "$rootDir/spec/spec.yml")
}

afterEvaluate {
    if (tasks.findByName("compileSpec") != null) {
        tasks.openApiGenerate {
            dependsOn("compileSpec")
        }
    }
}