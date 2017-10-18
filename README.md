libmsgs-android
=======================

To use this library in you application include this line in your `build.gradle`:

```
compile 'io.msgs:android-client:1.1@aar'
```

If you want to use an OkHttp client instead of the default one, use

```
compile 'io.msgs:android-client-okhttp:1.1@aar'
```
instead, and call `setClient(new MsgsOkHttpClient(okHttpClient))` on your Msgs client builder to use OkHTTP for executing the API calls.