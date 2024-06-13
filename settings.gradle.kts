pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven ( url ="https://jitpack.io" )
        maven(url = "https://artifactory.img.ly/artifactory/imgly")
        jcenter() {
            content {
                includeModule("com.theartofdev.edmodo", "android-image-cropper")
            }
        }
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven ( url ="https://jitpack.io" )
        maven(url = "https://artifactory.img.ly/artifactory/imgly")
        jcenter() {
            content {
                includeModule("com.theartofdev.edmodo", "android-image-cropper")
            }
        }
    }
}

rootProject.name = "PhotoEditor"
include(":app")
 