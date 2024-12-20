import org.gradle.internal.impldep.org.codehaus.plexus.interpolation.reflection.ReflectionValueExtractor.evaluate

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        maven(url = "/Users/salih.kukrer/AndroidStudioProjects/native_communication_app/flutter_module/build/host/outputs/repo")
        maven(url = "https://storage.googleapis.com/download.flutter.io")
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        maven(url = "/Users/salih.kukrer/AndroidStudioProjects/native_communication_app/flutter_module/build/host/outputs/repo")
        maven(url = "https://storage.googleapis.com/download.flutter.io")
        google()
        mavenCentral()
    }
}

rootProject.name = "HybridCommunicationApp"
include(":app", ":flutter_module")

 