pluginManagement {
    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        
        google()
        mavenCentral()
    }
}

rootProject.name = "Rally"
include(":app")
 