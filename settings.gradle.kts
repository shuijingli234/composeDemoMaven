import java.util.Properties

rootProject.name = "composeDemoAndroidiOS"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// 加载 local.properties（优先级高于 gradle.properties，且不进入 git）
val localProperties = Properties().apply {
    val file = rootDir.resolve("local.properties")
    if (file.exists()) {
        load(file.inputStream())
    }
}

fun propertyOrEnv(key: String, envVar: String = ""): String {
    return localProperties.getProperty(key)
        ?: (extra.properties[key] as? String)
        ?: (if (envVar.isNotBlank()) System.getenv(envVar) else null)
        ?: ""
}

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
        val gprUser = propertyOrEnv("gpr.user", "GITHUB_USER")
        val gprKey = propertyOrEnv("gpr.key", "GITHUB_TOKEN")
        if (gprUser.isNotBlank() && gprKey.isNotBlank()) {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/shuijingli234/composeDemoMaven")
                credentials {
                    username = gprUser
                    password = gprKey
                }
            }
        }
        val aliyunRepoUrl = propertyOrEnv("aliyun.repo.url")
        if (aliyunRepoUrl.isNotBlank()) {
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
