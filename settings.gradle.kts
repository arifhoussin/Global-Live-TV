// ==========================================
// ⚙️ প্লাগইন এবং রিপোজিটরি রেজোলিউশন হাব
// ==========================================
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Global-Live-TV"
include(":app")
