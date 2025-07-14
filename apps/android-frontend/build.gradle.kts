// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.dagger.hilt.android") version "2.56.2" apply false
    id("dev.nx.gradle.project-graph") version "0.1.0"
}

allprojects {
    apply {
        plugin("dev.nx.gradle.project-graph")
    }
}
