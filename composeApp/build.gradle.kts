import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import com.codingfeline.buildkonfig.compiler.FieldSpec

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.codeFelineBuildConfig)
}

version = "1.0"
val productName = "PodiumStreamer"
val productNameSpace = "com.noemi.podium.streamer"

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_22)
        }
    }

    iosX64 {
        binaries.framework {
            baseName = "composeApp"
            binaryOption("bundleId", "composeApp")
        }
    }
    iosArm64 {
        binaries.framework {
            baseName = "composeApp"
            binaryOption("bundleId", "composeApp")
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "composeApp"
            binaryOption("bundleId", "composeApp")
        }
    }

    applyDefaultHierarchyTemplate()

    cocoapods {
        summary = "PodiumStreamer Kotlin/Native module"
        homepage = "https://github.com/noemibalazs/PodiumStreamer"
        podfile = project.file("../iosApp/Podfile")

        ios.deploymentTarget = "15.0"

        pod("Reachability", "~> 3.2")

        xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = NativeBuildType.RELEASE
    }

    sourceSets {

        androidMain.dependencies {

            implementation(libs.compose.ui.tooling)

            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.ktor.client.android)
            implementation(libs.coroutine.android)

            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
        }

        commonMain.dependencies {
            implementation(libs.compose.constraint)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.material3.windowsizeclass.multiplatform)
            implementation(libs.compose.navigation)

            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            api(libs.precompose)
            api(libs.mirego.connectivity)

            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel)

            implementation(libs.coroutine.core)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.negotiation)
            implementation(libs.ktor.serialization)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.serialization)

            implementation(libs.kermit)
            implementation(libs.kermit.koin)

            implementation(libs.json.serialization)
            implementation(libs.ksoup.html)
            implementation(libs.web.view)

            implementation(libs.coil.compose.core)
            implementation(libs.coil.compose)
            implementation(libs.coil.mp)
            implementation(libs.coil.network.ktor)
        }

        iosMain {
            kotlin.srcDir("build/generated/ksp/metadata")
            dependencies {
                implementation(libs.ktor.darwin)
            }
        }
    }
}

android {
    namespace = "com.noemi.podium.streamer"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.noemi.podium.streamer"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_22
        targetCompatibility = JavaVersion.VERSION_22
    }

    buildFeatures {
        compose = true
    }

    dependencies {
        debugImplementation(compose.uiTooling)
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspCommonMainMetadata", libs.room.compiler)
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

buildkonfig {
    packageName = "com.noemi.podium.streamer"

    defaultConfigs {
        val token: String = gradleLocalProperties(rootDir, providers).getProperty("MASTODON_TOKEN")

        require(token.isNotEmpty()) {
            "Register your access token in local.properties as `MASTODON_TOKEN`"
        }

        buildConfigField(FieldSpec.Type.STRING, "MASTODON_TOKEN", token)
    }
}
