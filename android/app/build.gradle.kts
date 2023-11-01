plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}



android {
    namespace = "com.beaker.recipeRoulette"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.beaker.recipeRoulette"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packaging {
        exclude("META-INF/INDEX.LIST")
        exclude("META-INF/DEPENDENCIES")
        // Add any other exclusions or configurations as needed
    }
}

dependencies {


    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-base:18.2.0")
    implementation("androidx.datastore:datastore-rxjava3:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("com.google.firebase:firebase-messaging:23.3.0")
    implementation("androidx.compose.ui:ui-desktop:1.6.0-alpha08")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //OAuth
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.cloud:google-cloud-vision:3.27.0")

    //Preserving login state
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore:1.0.0")
    implementation("androidx.datastore:datastore-preferences-rxjava3:1.0.0")

    //handling server reviews
    implementation ("com.squareup.picasso:picasso:2.5.2")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.google.code.gson:gson:2.10.1")

    implementation("io.grpc:grpc-okhttp:1.41.0")
    
    //handling FCM
    implementation(platform("com.google.firebase:firebase-bom:32.4.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")








}