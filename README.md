<p align="center">
  <img align="middle" src="https://raw.githubusercontent.com/zingat/andversion/art/andversionLogo.png">
</p>

<p align="center">
  
  <a href="https://bintray.com/zingatmobil/AndVersion/andversion/1.4.0">
    <img src="https://api.bintray.com/packages/zingatmobil/AndVersion/andversion/images/download.svg">
  </a>
  <a target="_blank" href="https://android-arsenal.com/api?level=14">
    <img src="https://img.shields.io/badge/API-14%2B-orange.svg">
  </a>
</p>

Andversion is a powerful system for displaying dialogs about releases notes in Android.

Andversion takes care of shows **What's new** dialog automaticly, so you don't have to. 
It will load json file from the network then compares `CurrentVersion` and `MinVersion` with local storage that are saved automaticly by AndVersion,
 and displays dialog if it is neccessary. 
It has two levels of scenario; first one is `forceUpdate` and another one is `checkNews`.

Andversion is a public library that is written by Zingat Android Team.
These instructions will help you to set up your development environment. 

If you want to help developing the app take a look to the contribution guidelines.

> This library uses [Material Dialog Library](https://github.com/afollestad/material-dialogs).
Thank you [Aidan Follestad](https://github.com/afollestad)

## Table of Contents
1. [What is happening on background?](https://github.com/zingat/AndVersion/#what-is-happening-on-background)
2. [Sample Json File](https://github.com/zingat/AndVersion/#sample-json-file)
    1. [CurrentVersion](https://github.com/zingat/AndVersion/#currentversion-)
    2. [MinVersion](https://github.com/zingat/AndVersion/#minversion-)
    3. [WhatsNew](https://github.com/zingat/AndVersion/#whatsnew-)
3. [Gradle Dependency](https://github.com/zingat/AndVersion/#gradle-dependency)   
4. [Usage](https://github.com/zingat/AndVersion/#usage)   
    1. [Andversion Implementation](https://github.com/zingat/AndVersion/#andversion-implementation)
    2. [Initiliaze](https://github.com/zingat/AndVersion/#initiliaze)
    3. [Calling Method Seperately](https://github.com/zingat/AndVersion/#calling-method-seperately)
        1. [checkForceUpdate()](https://github.com/zingat/AndVersion/#checkForceUpdate)
        2. [checkNews()](https://github.com/zingat/AndVersion/#checkNews)
    4. [Calling a single method for every conditions](https://github.com/zingat/AndVersion/#calling-a-single-method-for-every-conditions)     
        1. [checkUpdate](https://github.com/zingat/AndVersion/#checkUpdate)   
    5. [Customize title and button texts](https://github.com/zingat/AndVersion/#customize-title-and)      

## What is happening on background?
First time your app open, AndVersion checks whether `currentVersionNumber` saved to `SharedPreferences` or not.
If it can't find a saved `currentVersionNumber`, AndVersion saves your app version to `SharedPreferences` automaticly.

When you call one of public AndVersion methods for example `checkUpdate(OnCompletedListener listener)`, AndVersion sends a request to defined url that you write in `setUri(String uri)`method and handle a json data.
Then AndVersion parse the result, and compares version already saved in `SharedPreferences` and `CurrentVersion`.

* If your app version is smaller than `MinVersion` it shows a dialog that is written last updates and users can't continue without update the app.
* If your app version is bigger or equal than `MinVersion`, AndVersion continue process don't do anything.
* Now AndVersion compares your app version and saved `currentVersionNumber` value.
    * If your app version is bigger than `currentVersionNumber` it means you update the app and it looks the `CurrentVersionNumber` comes from server. 
    If your app version and `CurrentVersion` values are equals it means the user is using the app after own app was updated.
    Then AndVersion shows what is new Dialogs.
    
**All process is managed by AndVersion and you should only update your Json file on server when you update your app. AndVersion checks everyting instead of you decides to show what's new dialog.**

## Sample Json File
```json
{
  "AndVersion": {
    "CurrentVersion": 115,
    "MinVersion": 110,
    "WhatsNew": {
      "en": [
        "3D home tours (where offered by rental listing). Check back often for more 3D home tours as they are added for a more immersive experience!",
        "Minor enhancements to Schools information shown for a property",
        "Bug fixes and performance improvements"
      ],
      "tr": [
        "3D konut turu (danışman tarafından eklenmişse). Daha güzel deneyimleri için 3D ev turları için sık sık uygulamamızı kullanabilirsiniz.",
        "Bir emlak etrafında gösterilen okul bilgileri için iyileştirmeler",
        "Hata düzeltmeleri ve performans iyileştirmeleri"
      ]
    }
  }
}
```
#### CurrentVersion :
Your App's version code on Google Play. 
The value should be integer. 

#### MinVersion :
The lowest version code that you want to support. 
The value should be integer. 
If user's version is lower than **MinVersion**, AndVersion applies **forceupdate** protocol.

#### WhatsNew : 
The list of new features to show to the user. 
The values should be also a json object. 
It allows you to present new features of your application to user in different languages.     


## Gradle Dependency
The minimum API level supported by this library is API 14.
Add the dependency to your `build.gradle`:

```Gradle
dependencies {
    compile 'com.zingat:andversion:1.3.2'
}
```

## USAGE
Add **INTERNET** and **ACCESS_NETWORK_STATE** permissions to your app's Manifest:
```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

### Andversion Implementation

There are two way to implement AndVersion. 
You can check update and news separately using two methods or you can check all of them using a single method.

### Initiliaze

Integrating with Andversion is intended to be seamless and straightforward for most existing Android applications. There is a simple initialization step which occurs in your Application class:
```java
@Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        AndVersion.getInstance()
                  .setUri( ANDVERSION_URL )
                  .addHeader( "key", "value" ); 

}
```
To define URL and Http headers in Application class provides that they are defined only once during app lifecycle. 
See below for specific details on individual subsystems.

**Andversion_url** means the url where you keep the JSON file, like http://andversion.com/sample/demoAndroid.json.

### Calling Method Seperately

**Note :** Every time you have to set `setActivity( this )`method to show dialog successfully in Activity or Fragment.

#### checkForceUpdate()
This method controls that user version code is smaller than minimum version in JSON file.

If true the force update dialog will be displayed, If false `OnCompletedListener.onCompleted()` works.

```java
@Override
    protected void onResume() {
        super.onResume();

        AndVersion.getInstance()
        .setActivity(this)
        .checkForceUpdate( new OnCompletedListener() {
            @Override
            public void onCompleted() {
                // The process what you want to do after check update.
                // This part srunsed when any dialog is not shown.
                // In these cases you can continue to make app run.
            }
        } );

}
```
The suggestion is to call this method in splash screen.
Add closeDialog() method in onPause() method.

```java
 @Override
    protected void onPause() {
        super.onPause();
        AndVersion.getInstance()
                .closeDialog();
    }
```
**Sample Screenshot for force update dialog.**
![Screenshots](https://raw.githubusercontent.com/zingat/andversion/dev/art/forceUpdateShowcase.png)

#### checkNews()
This method is used for showing information dialog after update. Information dialog will be displayed only once after every update.

```java
@Override
    protected void onResume() {
        super.onResume();

        AndVersion.getInstance()
		        .setActivity(this)
                .checkNews();

}
```
The suggestion is to call this method in main screen.

**Sample Screenshot for whats new dialog**
![Screenshots](https://raw.githubusercontent.com/zingat/andversion/dev/art/whatsNewShowcase.png)

### Calling a single method for every conditions

#### checkUpdate() 
First of all you also initialze Andversion before call `checkUpdate()` method.
`checkUpdate()` method contains both behaviours of `checkNews()` and `checkForceUpdate()` methods.

First it checks force update conditions by checking the app version that is defined by user in gradle file and min version in json file.  
If it not shown a force dialog second step is to check the news. Then app can resume.

**Note :** When any error occurs onCompleted method will be called. 

```java
@Override
    protected void onResume() {
        super.onResume();
        
         AndVersion.getInstance()
		        .setActivity(this)
                .checkUpdate( new OnCompletedListener() {
                    @Override
                    public void onCompleted() {
                       // The process what you want to do after check update.
                    }
                } );

}
```
The suggestion is to call this method in splash screen. 

### Customize title and button texts

To customize dialog title and buttons text add the following xml codes in your app strings.xml.

```xml
<string name="andversion_forceupdate_title">What's New Waitin For You!</string>
<string name="andversion_whatsnew_title">News In This Version!</string>
<string name="andversion_update">Update</string>
<string name="andversion_exit_app">Exit</string>
<string name="andversion_ok">Ok</string>
```

<img align="right" src="https://raw.githubusercontent.com/zingat/andversion/art/zingatLogo.png" width="320">
