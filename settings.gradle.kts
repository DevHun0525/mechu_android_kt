pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        //naver
//        maven("https://repository.map.naver.com/archive/maven")
        //kakao
        //map
        maven(url = uri("https://devrepo.kakao.com/nexus/repository/kakaomap-releases/"))
        //kakao login
        maven { url = java.net.URI("https://devrepo.kakao.com/nexus/content/groups/public/") }

//        maven{url = uri("https://devrepo.kakao.com/nexus/content/groups/public/") }
    }
}

rootProject.name = "Mechu"
include(":app")
