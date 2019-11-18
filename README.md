libmsgs-android
=======================

To use this library in you application include this line in your `build.gradle`:

```
implementation 'io.msgs:android-client:1.1.5@aar'
```

If you want to use an OkHttp client instead of the default one, use

```
implementation 'io.msgs:android-client-okhttp:1.1.5@aar'
```
instead, and call `setClient(new MsgsOkHttpClient(okHttpClient))` on your Msgs client builder to use OkHTTP for executing the API calls.


## Uploading a new version to BinTray

This article explains all the steps from the beginning to the end: https://inthecheesefactory.com/blog/how-to-upload-library-to-jcenter-maven-central-as-dependency/en
To sum it all up, here's an example for publishing a new version of the `android-client-okhttp` module.

  1. Update the version name in the build.gradle on the top and also in the bottom. No need to change the version code.
  2. Run the following command to check if it installs correctly: `./gradlew :android-client-okhttp:install`
  3. Make sure you don't get any errors after running the previous command
  4. To upload the artifacts to BinTray, run the command: `./gradlew :android-client-okhttp:bintrayUpload`
  5. Sadly the plugin does not display if there was an error while uploading the artifacts, so please double check on the BinTray site if it was indeed published. If not, check if you have access to BinTray and have added the correct values to your local.properties. Ask Daniel for access or help if this does not work you.
