plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation ("com.squareup.retrofit2:converter-gson:2.5.0")
    implementation ("org.apache.xmlgraphics:batik-swing:1.14")

}

tasks.test {
    useJUnitPlatform()
}