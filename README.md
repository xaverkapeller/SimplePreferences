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
String text = preferences.getText();
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
 
You can easily set a default value by annotating the getters in your interface with one of these annotations:

 - `@DefaultStringValue`
 - `@DefaultIntegerValue`
 - `@DefaultLongValue`
 - `@DefaultBooleanValue`
 - `@DefaultFloatValue`
 
```java
@DefaultStringValue("Some default text")
public String getText();
```

You can also use `@DefaultResourceValue` to set some localized text as default value.
```java
@DefaultResourceValue(R.string.localized_text)
public String getText();
```
 
By default every interface will use its own `SharedPreferences` instance, but you can pass a custom preferences name or even a whole `SharedPreferences` instance into the `PreferencesFactory` if you want.

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

How it works
------

The module SimplePreferencesCompiler is implementing an annotation processor. This processor will be executed everytime you compile your project. It will look for interfaces in your source code that have been annotated with the `@Preferences` annotation and if it finds any will generate an approrpirate implementation of those interfaces for you! For example for an interface like this:

```java
@Preferences
public interface ExamplePreferences {

    public void setText(String name);
    @DefaultResourceValue(R.string.localized_default_text)
    public String getText();

    public void setCount(int count);
    @DefaultIntegerValue(27)
    public int getCount();
}
```

The annotation processor would generate an implementation that looks something like this:

```java
public final class ExamplePreferences$$Impl implements ExamplePreferences {
  private final android.content.SharedPreferences _a;
  private final android.content.Context _b;
  public ExamplePreferences$$Impl(android.content.SharedPreferences a, android.content.Context b) {
    _a = a;
    _b = b;
  }
  public int getCount() {
    return _a.getInt("Count", 27);
  }
  public void setText(String a) {
    _a.edit().putString("Text", a).commit();
  }
  public void setCount(int a) {
    _a.edit().putInt("Count", a).commit();
  }
  public String getText() {
    return _a.getString("Text", _b.getString(2131361813));
  }
}
```

As you can see the name of the getter and setter methods is used as a key! So be careful when changing the name of those methods. When you use the `PreferencesFactory` to create an instace of your interface it is looking for the implementation at runtime like this:

```java
final String implName = interfaceClass.getName() + "$$Impl";
final Class<?> implClass = Class.forName(implName);
T instance = (T) implClass.getConstructor(SharedPreferences.class, Context.class).newInstance(sharedPreferences, context);
```

If you'd like to know more about how this library works feel free to study the source code yourself!
