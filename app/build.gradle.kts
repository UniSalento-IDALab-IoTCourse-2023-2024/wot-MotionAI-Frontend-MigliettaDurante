/*
 * Copyright (c) 2022(-0001) STMicroelectronics.
 * All rights reserved.
 * This software is licensed under terms that can be found in the LICENSE file in
 * the root directory of this software component.
 * If no LICENSE file comes with this software, it is provided AS-IS.
 */

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.har.migliettadurante"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.har.migliettadurante"
        minSdk = 26
        targetSdk = 34
        versionCode = 4
        versionName = "5.2.4"

        vectorDrawables { useSupportLibrary = true }

        manifestPlaceholders["appAuthRedirectScheme"] = "stdemo"
    }

    hilt {
        enableAggregatingTask = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeCompiler {
        enableStrongSkippingMode = true
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.generateKotlin", "true")
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {

    implementation(project(":st_blue_sdk"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.composeUiTooling)
    implementation(libs.bundles.network)
    implementation(libs.androidx.material2)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation (libs.exoplayer)
    implementation(libs.microsoft.onnxruntime.android)

    debugImplementation(libs.androidx.compose.uitestmanifest)
}
