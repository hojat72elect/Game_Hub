import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(PLUGIN_GRADLE_VERSIONS) version versions.gradleVersionsPlugin
    id(PLUGIN_DETEKT) version versions.detektPlugin
    id(PLUGIN_KTLINT) version versions.ktlintPlugin
}

buildscript {
    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${versions.gradlePluginVersion}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.kotlin}")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${versions.daggerHilt}")
        classpath("com.google.protobuf:protobuf-gradle-plugin:${versions.protobufPluginVersion}")
        classpath( "com.github.ben-manes:gradle-versions-plugin:${versions.gradleVersionsPlugin}")
    }
}

detekt {
    parallel = true
    buildUponDefaultConfig = true
    config = files("config/detekt/detekt.yml")
}

tasks.withType<Detekt>().configureEach {
    reports.html.required.set(true)
}

allprojects {
    apply(plugin = PLUGIN_DETEKT)
    apply(plugin = PLUGIN_KTLINT)

    repositories {
        mavenCentral()
        google()
        maven { setUrl("https://jitpack.io") }
    }

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set(versions.ktlint)
        android.set(true)
        outputToConsole.set(true)

        filter {
            // https://github.com/JLLeitschuh/ktlint-gradle/issues/266#issuecomment-529527697
            exclude { fileTreeElement -> fileTreeElement.file.path.contains("generated/") }
        }

        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
        }
    }

    // Without the below block, a build failure was happening when
    // running ./gradlew connectedAndroidTest.
    // See: https://github.com/mockito/mockito/issues/2007#issuecomment-689365556
    configurations.all {
        resolutionStrategy.force("org.objenesis:objenesis:2.6")
    }
}

subprojects {

    tasks.withType<KotlinCompile>().all {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
                "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
                "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
                "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
                "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                "-opt-in=androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi",
                "-opt-in=com.google.accompanist.pager.ExperimentalPagerApi",
            )

            jvmTarget = appConfig.kotlinCompatibilityVersion.toString()
        }
    }

    plugins.withId(PLUGIN_KOTLIN_KAPT) {
        extensions.findByType<KaptExtension>()?.run {
            correctErrorTypes = true
        }
    }

    // https://stackoverflow.com/a/70348822/7015881
    // https://issuetracker.google.com/issues/238425626
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "androidx.lifecycle" && requested.name == "lifecycle-viewmodel-ktx") {
                useVersion(deps.androidX.viewModel)
            }
        }
    }
}