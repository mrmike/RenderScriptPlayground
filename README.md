
About
---------

**RenderScriptPlayground** is an exemplary Android app showing how to use RenderScript with Android Studio and Gradle. Application is using RenderScript support library so it should works with Android Froyo (2.2). 
Getting Started
------------

#### 1. Clone project
```
git clone https://github.com/mrmike/RenderScriptPlayground.git
```

#### 2. Define environmental variables
```
~$ export ANDROID_HOME=path/to/android/sdk
~$ export ANDROID_NDK_HOME=path/to/android/ndk
```

If you do not want to use env variables you can always create local.properties file in project root folder.
```
// local.properties
sdk.dir=path/to/android/sdk
ndk.dir=path/to/android/ndk
```

### 3. Bulding project
```
~$ ./gradlew assemble
```

### 4. Installing app
```
~$ ./gradlew installArmDebug
```

Example
------------

![Image](./rs_blur_example.png?raw=true)![Image](./rs_mono_example.png?raw=true)

Photo: http://www.flickr.com/photos/29468339@N02/4542603519