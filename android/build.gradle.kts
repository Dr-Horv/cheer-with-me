val hiltVersion by extra("1.0.0-alpha02")

buildscript {

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build", "gradle", "7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
        classpath("com.google.dagger", "hilt-android-gradle-plugin", "2.40")
    }
}
