KTHULU-ANDROID-SDK
============

This application Kthulu Wallet Application using Kthulu Library

Introduction
------------

Pre-requisites
--------------
* JAVA 11
* GRADLE 7.4.1
* KOTLIN 1.8.0

Export (jitpack Release)
--------------------
1. Releases --> Draft a new release
   Choose a tag, Release title write a new version --> Publish release

2. https://jitpack.io/ --> companyAbcDev/kthuluAndroidSDK Look up

3. latest version Get it
   (white(loading) --> Green(OK))

Export (AAR Release)
--------------------
1.  Gradle Script --> build.gradle(Module:app)

    plugins {
        ~~id 'com.android.applcation'~~ (change) --> id 'com.android.library'
        ...
    }

    defaultConfig {
        ~~applicationId "com.example.android_sdk"~~ (delete)
    }

2. Sync Project with Gradle Files or Sync Now

3. Build --> Make Module 'androidsdk.app' or   
   terminal : ./gradlew assembleRelease --stacktrace

4. aar location : (Project location)/build/output/aar/app-release.aar

