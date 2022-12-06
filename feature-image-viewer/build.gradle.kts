plugins {
    androidLibrary()
    gamedgeAndroid()
    kotlinKapt()
    ksp()
    daggerHiltAndroid()
}

android {
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = versions.compose
    }
}

dependencies {
    implementation(project(deps.local.commonDomain))
    implementation(project(deps.local.core))
    implementation(project(deps.local.commonUi))

    implementation(deps.kotlin.coroutines)

    implementation(deps.compose.ui)
    implementation(deps.compose.tooling)
    implementation(deps.compose.foundation)
    implementation(deps.compose.activity)
    implementation(deps.compose.material)
    implementation(deps.compose.runtime)
    implementation(deps.compose.hilt)
    implementation(deps.compose.accompanist.pager)
    implementation(deps.compose.accompanist.systemUi)

    implementation(deps.commons.core)
    implementation(deps.commons.ktx)

    implementation(deps.misc.coil)
    implementation(deps.misc.zoomable)

    implementation(deps.google.daggerHiltAndroid)
    kapt(deps.google.daggerHiltAndroidCompiler)

    implementation(deps.misc.hiltBinder)
    ksp(deps.misc.hiltBinderCompiler)

    testImplementation(project(deps.local.commonTesting))
    testImplementation(deps.testing.jUnit)
    testImplementation(deps.testing.truth)
    testImplementation(deps.testing.mockk)
    testImplementation(deps.testing.coroutines)
    testImplementation(deps.testing.turbine)

    androidTestImplementation(deps.testing.testRunner)
    androidTestImplementation(deps.testing.jUnitExt)
}
