# SimplePreferences

The preferences you always wanted on Android.

 - **Quick to setup**: You can get started literally as quickly as you can create an interface. As soon as you add SimplePreferences to your project it starts working for you.
 - **Simplifies your code**: Never deal with SharedPreferences again. SimplePreferences does all the work for you.
 - **No runtime overhead**: SimplePreferences uses compile time code generation to generate the boilerplate for your preferences. It runs just as fast as if you had written it yourself without you having to do anything!
 - **Don't worry about ProGuard**: SimplePreferences does not require any ProGuard rules.
 
## How to add it to your project

Just add these two lines to the dependencies closure of your module:

```groovy
api 'com.github.wrdlbrnft:simple-preferences:0.4.0.9'
annotationProcessor 'com.github.wrdlbrnft:simple-preferences-processor:0.4.0.9'
```

## How to use it

Just create an interface like the one below with all the getters and setters for the preferences you need and then annotate it with `@Preferences`:

```java
@Preferences("Example")
public interface ExamplePreferences {

    void setText(String text);
    String getText();

    void setCount(int count);
    int getCount();
}
```

The String you enter in `@Preferences` will be used to set the name of the preferences file so don't change it when refactoring! That would cause all saved data to be lost (at least until you change it back). 

After creating the interface SimplePreferences generates a factory class which you can use to create an instance of your preferences interface. The factory classes are always named like this: `<Name of the Interface>Factory`.

```java
ExamplePreferences preferences = ExamplePreferencesFactory.newInstance(context);
```

**NOTE**: The factory classes are generated when you build your project so it should become usable after you have build your project at least once. 

And that's it! Now you can use the setters and getters to save and load data. Any saved data will automatically be persisted on the device.

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
String getText();
```

You can also use `@DefaultResourceValue` to set some localized text as default value.
```java
@DefaultResourceValue(R.string.localized_text)
String getText();
```
