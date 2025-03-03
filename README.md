libmsgs-android
=======================

To use this library in you application you need to include the aar file in your application.
Use the aar of the -okhttp module to use the OkHttp client instead of the default one.

## Creating a new aar

To build the default client:
```
./gradlew :android-client:assembleRelease
```
And then locate the aar file at `android-client/build/outputs/aar/android-client-release.aar`.

To build the OkHttp version:
```
./gradlew :android-client-okhttp:assembleRelease
```
And then locate the aar file at `android-client-okhttp/build/outputs/aar/android-client-okhttp-release.aar`.