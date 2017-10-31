Changelog
=========

## 1.1.2

The DELETE call returned an empty string body on success, which would throw an exception when being parsed as JSON. This is now converted to an empty JSON object result.

## 1.1.1

Added the properties `createdAt` and `updatedAt` to the `Channel` class in the `v2` package.

## 1.1

Renamed the package to `android-client`, and added an `android-client-okhttp` extension (which depends on the former).
With the OkHTTP extension, you can replace the transport layer from `HttpUrlConnection` to the `OkHttpClient` provided by you.

## 1.0

Initial version which is the first one being published on BinTray.


 