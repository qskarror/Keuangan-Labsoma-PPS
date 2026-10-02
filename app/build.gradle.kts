plugins {
    id("com.android.application")
}

android {
    namespace = "id.labsoma.keuangan"
    compileSdk = 34

    defaultConfig {
        applicationId = "id.labsoma.keuangan"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
