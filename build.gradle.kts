import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "2.2.3.RELEASE"
  id("io.spring.dependency-management") version "1.0.8.RELEASE"
  kotlin("jvm") version "1.3.61"
  kotlin("plugin.spring") version "1.3.61"
  kotlin("plugin.jpa") version "1.3.61"
}

group = "com.ilinaraducristian"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_11

val developmentOnly by configurations.creating
configurations {
  runtimeClasspath {
    extendsFrom(developmentOnly)
  }
}

repositories {
  mavenCentral()
}

dependencies {
  // MOEA Framework
  implementation("org.moeaframework:moeaframework:2.13")

  // Spring Framework
//	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
//	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
//	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
  implementation("org.springframework.boot:spring-boot-starter-amqp")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.session:spring-session-data-redis")
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  runtimeOnly("mysql:mysql-connector-java")

  // JWT
  implementation("io.jsonwebtoken:jjwt-api:0.10.7")
  runtimeOnly("io.jsonwebtoken:jjwt-impl:0.10.7")
  runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.10.7")

  // Embedded servers
//  implementation("it.ozimov:embedded-redis:0.7.2")
  runtimeOnly("com.h2database:h2")

  // Kotlin
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

  // Tests
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
  }
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
    jvmTarget = "1.8"
  }
}