plugins {
	java
  application
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("checkstyle")
}

group = "aim"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(23)
	}
}

application {
    mainClass.set("aim.hotel_booking.HotelBookingApplication")
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

tasks.withType<ProcessResources> {
	duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<JavaExec> {
	mainClass.set("aim.hotel_booking.HotelBookingApplication")
}

sourceSets {
	main {
		java {
			srcDirs("${projectDir}/generated/src/main/java")
		}
		resources {
			srcDirs("${projectDir}/generated/src/main/resources")
		}
	}
}

repositories {
	gradlePluginPortal()
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.springframework.security:spring-security-messaging:6.4.3")
	implementation("org.postgresql:postgresql")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
	implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
	implementation("org.glassfish:jakarta.el:5.0.0-M1")
	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("jakarta.validation:jakarta.validation-api:3.0.2")
	implementation("io.swagger.core.v3:swagger-annotations:2.2.29")
	implementation("org.mapstruct:mapstruct:1.6.3")
	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
	implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3");
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
	compileOnly("org.projectlombok:lombok:1.18.32")
	annotationProcessor("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks {
	named<Checkstyle>("checkstyleMain") {
		classpath = sourceSets.main.get().compileClasspath
		source = fileTree("src/main/java") {
			exclude("**/generated/**")
		}
	}
}
