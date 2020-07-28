plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("kotlin-android")
}

val composeVersion = "0.1.0-dev15"
val coroutinesVersion = "1.3.8-1.4.0-rc"
val roomVersion = "2.2.5"
val archLifecycleVersion = "2.2.0"
val filamentVersion = "1.8.0"
val navigationVersion = "2.3.0"

dependencies {
    implementation(kotlin("stdlib"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    implementation("com.google.android.material:material:1.1.0")

    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.appcompat:appcompat:1.1.0")

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.android.gms:play-services-maps:17.0.0")
    implementation("androidx.annotation:annotation:1.1.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    kapt("androidx.room:room-compiler:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")

    implementation("androidx.lifecycle:lifecycle-extensions:$archLifecycleVersion")
    kapt("androidx.lifecycle:lifecycle-common-java8:$archLifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$archLifecycleVersion")

    implementation("androidx.compose.animation:animation:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.foundation:foundation-layout:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.runtime:runtime:$composeVersion")
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.ui:ui-tooling:$composeVersion")

    implementation("com.google.android.filament:filament-android:$filamentVersion")
    implementation("com.google.android.filament:filament-utils-android:$filamentVersion")
    implementation("com.google.android.filament:gltfio-android:$filamentVersion")

    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")

    implementation("com.google.android.gms:play-services-auth:18.1.0")
}

android {
    compileSdkVersion(29)
    buildToolsVersion = "29.0.3"

    defaultConfig {
        applicationId = "dev.fredag.cheerwithme"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"

        javaCompileOptions {
            annotationProcessorOptions {
                argument("room.incremental", "true")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerVersion = "1.4.0-dev-withExperimentalGoogleExtensions-20200720"
        kotlinCompilerExtensionVersion = composeVersion
    }

    packagingOptions {
        exclude("META-INF/atomicfu.kotlin_module")
    }

    aaptOptions {
        noCompress("filamat", "ktx", "glb")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = listOf("-Xallow-jvm-ir-dependencies", "-Xskip-prerelease-check")
    }
}