val hiltVersion by extra("1.0.0-alpha02")

buildscript {

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // other plugins...
        classpath("com.google.dagger", "hilt-android-gradle-plugin", "2.40")
        classpath("com.android.tools.build", "gradle", "7.0.0-beta04")
    }
}

plugins {
    id("com.android.application") version "7.0.2" apply false
    //id("com.google.dagger:hilt-android-gradle-plugin") version "1.0.0-alpha02" apply false
    kotlin("android") version "1.5.31" apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}