# Popular Movies App

Welcome to the soon-to-be award-willing Popular Movies App. 

## Before Building

*The Movie Db API Key is required.*

In order for the app to function properly an API key for themoviedb.org must be included with the build.

You can obtain a key via the following [instructions](https://www.themoviedb.org/documentation/api), and include the unique key for the build by adding the following line to
[USER_HOME]/.gradle/gradle.properties

`MyTheMovieDbApiKey="<UNIQUE_API_KEY">`

## Building

To build this project on the command line, the following commands are available

```bash
./gradlew assembleDebug
```
See ```./gradlew tasks``` for a full listing of available build commands.

## Tests

### Local Unit Tests

Robolectric Activity lifecycle and unit tests can be ran using the following command.

```bash
./gradlew test
```

### Instrumentation Tests

Espresso has been implemented into this project in order to simulate user interaction to test UI.
Espresso tests run on a physical device or an emulator/Genymotion.

```bash
./gradlew connectedAndroidTest
```