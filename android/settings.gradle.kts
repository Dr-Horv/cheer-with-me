rootProject.name = "Cheer With Me"

dependencyResolutionManagement() {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        //maven(url = "https://dl.bintray.com/kotlin/kotlin-eap/")
        mavenCentral()
        gradlePluginPortal()
        jcenter()
    }
}

include(":app")
