plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.tarkov"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tarkov"
        minSdk = 21
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }
    packagingOptions {
        resources {
            excludes += setOf("META-INF/DEPENDENCIES")
        }
    }

    configurations.all {
        resolutionStrategy {
            force ("com.google.api-client:google-api-client-jackson2:1.32.1")
        }
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment:2.7.6")
    implementation("androidx.navigation:navigation-ui:2.7.6")

    implementation("com.android.volley:volley:1.2.1")
    implementation("androidx.core:core:1.13.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Обновите зависимость RecyclerView на AndroidX
    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    // Добавьте зависимости Glide (переход на AndroidX для Glide не требуется)
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.squareup.picasso:picasso:2.71828")

    implementation ("commons-io:commons-io:2.15.1")
    implementation ("org.jsoup:jsoup:1.14.3")


    implementation ("androidx.core:core-ktx:1.12.0")

    implementation ("com.google.android.material:material:1.11.0") // Для SwitchMaterial
    implementation ("androidx.appcompat:appcompat:1.6.1") // Для SwitchCompat
    implementation ("androidx.appcompat:appcompat:1.6.1")



    implementation ("com.google.api-client:google-api-client-android:1.32.1")
    implementation ("com.google.api-client:google-api-client-gson:1.32.1")
    implementation ("com.google.apis:google-api-services-youtube:v3-rev222-1.25.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("com.google.api-client:google-api-client-jackson2:1.32.1")

//    implementation ("com.google.http-client:google-http-client-android:1.39.0")
//    implementation ("com.google.api-client:google-api-client-gson:1.39.0")
    implementation ("com.google.http-client:google-http-client-jackson2:1.41.0")

    implementation ("com.google.apis:google-api-services-youtube:v3-rev305-1.25.0")

    // https://mavenlibs.com/maven/dependency/com.google.apis/google-api-services-youtube
    implementation ("com.google.apis:google-api-services-youtube:v3-rev20230816-2.0.0")

    implementation ("com.davemorrissey.labs:subsampling-scale-image-view-androidx:3.10.0")
    implementation ("com.google.android.material:material:1.4.0")
    implementation ("androidx.core:core-ktx:1.7.0")
    implementation ("androidx.legacy:legacy-support-v4:+")
    implementation ("androidx.lifecycle:lifecycle-common:2.5.1")
}