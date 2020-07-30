val hiltVersion by extra("1.0.0-alpha02")

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // other plugins...
        classpath("com.google.dagger","hilt-android-gradle-plugin", "2.28.3-alpha")
    }
}

plugins {
    id("com.android.application") version "4.2.0-alpha06" apply false
    //id("com.google.dagger:hilt-android-gradle-plugin") version "1.0.0-alpha02" apply false
    kotlin("android") version "1.4.0-rc" apply false
}

allprojects {
    repositories {
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap/")
        jcenter()
        google()
    }
}