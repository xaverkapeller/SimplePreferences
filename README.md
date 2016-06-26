# SimplePreferences
===========

The preferences you always wanted on Android.

 - **Quick to setup**: You can get started literally as quickly as you can create an interface. As soon as you add SimplePreferences to your project it starts working for you.
 - **Simplifies your code**: Never deal with SharedPreferences again. SimplePreferences does all the work for you.
 - **No runtime overhead**: SimplePrefernences uses compile time code generation to generate the boilerplate for your preferences. It runs just as fast as if you had written it yourself without you having to do anything!
 - **Don't worry about ProGuard**: This library doesn't need any ProGuard rules.
 
## How to add it to your project

If you are using the Jack compiler just add these two lines to the dependencies closure of your module:

```groovy
compile 'com.github.wrdlbrnft:simple-preferences:0.1.0.0'
annotationProcessor 'com.github.wrdlbrnft:simple-preferences-processor:0.1.0.0'
```

If you are not using Jack you can use the android-apt Grade plugin and its apt configuration instead of the `annotationProcessor` configuration.

## How to use it

Just create an interface like the one below with all the getters and setters for the preferences you need and then annotate it with `@Preferences`:

```java
@Preferences
public interface ExamplePreferences {

    public void setText(String text);
    public String getText();

    public void setCount(int count);
    public int getCount();
}
```

After that SimplePreferences generates a factory class which you can use to create an instance of your preferences interface:

```java
ExamplePreferences preferences = ExamplePreferencesFactory.newInstance(context);
```

And that's it! You can use the setters and getters and anything you save will be persisted with `SharedPreferences`.

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
