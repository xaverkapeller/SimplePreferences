SimplePreferences
===========

SimplePreferences is a library which uses compile time annotation processing to make preferences simple and easy to use.

How to use it
-----

Just create an interface like this with all the getters and setters for the preferences you need and then annotate it with `@Preferences`:

```java
@Preferences
public interface ExamplePreferences {

    public void setText(String name);
    public String getText();

    public void setCount(int count);
    public int getCount();
}
```

You can then create an instance of `ExamplePreferences` by using the `PreferencesFactory`:

```java
ExamplePreferences preferences = PreferencesFactory.create(ExamplePreferences.class, context);
```

The rest is handled by the library, you can just use the getters and setters like you normally would:

```java
String name = preferences.getText();
preferences.setText(someOtherText);

int count = preferences.getCount();
preferences.setCount(count + 1);
```

But beware! At the moment only a few primitive types and `String` are supported! The full list is:

 - `int`
 - `boolean`
 - `long`
 - `float`
 - `String`

Installation
--------

 **1)** Just download this library and add the three modules SimplePreferences, ProcessorUtils and SimplePreferencesCompiler to your Android project.

 **2)** The top of the build.gradle file of your app needs to look like this:

```
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.+'
    }
}

apply plugin: 'com.android.application'
apply plugin: 'android-apt'
...
```

 **3)** In the dependencies add these two lines at the bottom:

```
apt project(':SimplePreferencesCompiler')
compile project(':SimplePreferences')
```

The whole build.gradle should then look something like this:

```
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.+'
    }
}

apply plugin: 'com.android.application'
apply plugin: 'android-apt'

android {
    compileSdkVersion 21
    buildToolsVersion "21.1.2"

    defaultConfig {
        applicationId "com.example.app"
        minSdkVersion 11
        targetSdkVersion 21
        versionCode 1
        versionName "1.0"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:appcompat-v7:21.0.3'

    apt project(':SimplePreferencesCompiler')
    compile project(':SimplePreferences')
}
```

After that you are all set! Just annotate your preference interfaces with `@Preferences` and you are good to go!

