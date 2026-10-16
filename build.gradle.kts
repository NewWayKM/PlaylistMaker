// Top-level build file where you can add configuration options common to all sub-projects/modules.

// 1. Версия Kotlin Gradle Plugin для встроенного Kotlin в AGP
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.21")
    }
}

// 2. Плагины проекта
plugins {
    alias(libs.plugins.android.application) apply false
}