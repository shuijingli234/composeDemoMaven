import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    `maven-publish`
}

// 加载 local.properties（优先级高于 gradle.properties，且不进入 git）
val localProps = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        load(file.inputStream())
    }
}

fun resolveProperty(key: String, envVar: String = ""): String {
    return localProps.getProperty(key)
        ?: (project.findProperty(key) as? String)
        ?: (if (envVar.isNotBlank()) System.getenv(envVar) else null)
        ?: ""
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        publishLibraryVariants("release", "debug")
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "org.example.sdk"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

val sdkGroupId = "org.example.project"
val sdkArtifactId = "maven-publish-test-sdk"
val sdkVersion = "1.0.4-fork"

group = sdkGroupId
version = sdkVersion

publishing {
    repositories {
        maven {
            name = "GitHubPages"
            url = uri(layout.buildDirectory.dir("repo"))
        }
        val aliyunUrl = resolveProperty("aliyun.repo.url")
        if (aliyunUrl.isNotBlank()) {
            maven {
                name = "Aliyun"
                url = uri(aliyunUrl)
                credentials {
                    username = resolveProperty("aliyun.user")
                    password = resolveProperty("aliyun.password")
                }
            }
        }
    }
}

afterEvaluate {
    val gprUser = resolveProperty("gpr.user", "GITHUB_USER")
    val gprKey = resolveProperty("gpr.key", "GITHUB_TOKEN")
    if (gprUser.isNotBlank() && gprKey.isNotBlank()) {
        publishing {
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/shuijingli234/composeDemoMaven")
                    credentials {
                        username = gprUser
                        password = gprKey
                    }
                }
            }
        }
    }
}

tasks.register<Exec>("publishToGitHubPages") {
    group = "publishing"
    description = "Publish Maven artifacts to GitHub Pages repository"
    dependsOn("publishAllPublicationsToGitHubPagesRepository")
    val repoDir = layout.buildDirectory.dir("repo").get().asFile
    doFirst {
        if (!repoDir.exists()) {
            throw GradleException("Repository directory not found. Run publish first.")
        }
    }
    workingDir = rootProject.projectDir
    commandLine(rootProject.file("publish-maven.sh").absolutePath, "--skip-publish")
}

if (resolveProperty("gpr.user", "GITHUB_USER").isNotBlank()) {
    tasks.register("publishToGitHubPackages") {
        group = "publishing"
        description = "Publish Maven artifacts to GitHub Packages"
        dependsOn("publishAllPublicationsToGitHubPackagesRepository")
    }
}

if (resolveProperty("aliyun.repo.url").isNotBlank()) {
    tasks.register("publishToAliyun") {
        group = "publishing"
        description = "Publish Maven artifacts to Aliyun Package Repository"
        dependsOn("publishAllPublicationsToAliyunRepository")
    }
}

afterEvaluate {
    publishing {
        publications.withType<MavenPublication> {
            when (name) {
                "kotlinMultiplatform" -> artifactId = sdkArtifactId
                "androidRelease" -> artifactId = "$sdkArtifactId-android"
                "androidDebug" -> artifactId = "$sdkArtifactId-android-debug"
                else -> artifactId = artifactId.replace(project.name, sdkArtifactId)
            }
            pom {
                name.set(sdkArtifactId)
                description.set("A simple test SDK for Maven publishing demo")
                url.set("https://github.com/shuijingli234/composeDemoMaven")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("shuijingli234")
                        name.set("shuijingli234")
                    }
                }
            }
        }
    }
}
