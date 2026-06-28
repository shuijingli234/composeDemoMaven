import java.util.Properties

rootProject.name = "composeDemoAndroidiOS"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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
        // 方式一：本地 Maven 缓存（开发调试用）
        // mavenLocal()

        // 方式二：GitHub Packages（需要 token）
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

        // 方式三：阿里云制品仓库（需要凭据）
        // val aliyunRepoUrl = propertyOrEnv("aliyun.repo.url")
        // if (aliyunRepoUrl.isNotBlank()) {
        //     maven {
        //         name = "Aliyun"
        //         url = uri(aliyunRepoUrl)
        //         credentials {
        //             username = propertyOrEnv("aliyun.user")
        //             password = propertyOrEnv("aliyun.password")
        //         }
        //     }
        // }

        // 方式四：GitHub Pages（静态站点，无需认证）
        // maven {
        //     name = "GitHubPages-composeDemoMaven"
        //     url = uri("https://shuijingli234.github.io/composeDemoMaven/")
        // }
        // maven {
        //     name = "GitHubPages-kotlinx-datetime"
        //     url = uri("https://shuijingli234.github.io/kotlinx-datetime/")
        // }

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
