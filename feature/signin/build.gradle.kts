// :feature:signin 모듈의 build.gradle.kts
import java.util.Properties

plugins {
    id("gwangsan.android.feature")
    id("gwangsan.android.hilt")
    id("gwangsan.android.compose")
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}
val spotifyClientId = localProperties.getProperty("SPOTIFY_CLIENT_ID") ?: "\"\""

android {
    namespace = "com.school_of_company.signin"

    defaultConfig {
        manifestPlaceholders["SPOTIFY_CLIENT_ID"] = spotifyClientId.replace("\"", "")
    }
}

dependencies {
    implementation(project(":feature:post"))
    implementation(project(":core:data"))
    implementation("com.spotify.android:auth:2.1.0")
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.kt)
    implementation(libs.androidx.navigation.compose.android)
    implementation(libs.androidx.exifinterface)
    implementation(libs.play.services.auth)
}