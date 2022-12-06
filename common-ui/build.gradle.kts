plugins {
    androidLibrary()
    gamedgeAndroid()
    kotlinKapt()
    ksp()
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
    implementation(project(deps.local.core))

    implementation(deps.compose.ui)
    implementation(deps.compose.tooling)
    implementation(deps.compose.foundation)
    implementation(deps.compose.activity)
    implementation(deps.compose.runtime)
    implementation(deps.compose.material)
    implementation(deps.compose.accompanist.systemUi)

    implementation(deps.commons.core)
    implementation(deps.commons.ktx)

    implementation(deps.misc.coil)

    implementation(deps.google.daggerHiltAndroid)
    kapt(deps.google.daggerHiltAndroidCompiler)

    implementation(deps.misc.hiltBinder)
    ksp(deps.misc.hiltBinderCompiler)

    testImplementation(deps.testing.jUnit)
    androidTestImplementation(deps.testing.jUnitExt)
}
