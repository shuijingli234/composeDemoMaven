rootProject.name = "composeDemoAndroidiOS"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/shuijingli234/composeDemoAndroidiOS")
            credentials {
                username = (extra.properties["gpr.user"] as? String) ?: System.getenv("GITHUB_USER") ?: ""
                password = (extra.properties["gpr.key"] as? String) ?: System.getenv("GITHUB_TOKEN") ?: ""
            }
        }
        val aliyunRepoUrl = (extra.properties["aliyun.repo.url"] as? String)
        if (!aliyunRepoUrl.isNullOrBlank()) {
            maven {
                name = "Aliyun"
                url = uri(aliyunRepoUrl)
            }
        }
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")
include(":mavenPublishTestSDK")
