![Logo](https://raw.githubusercontent.com/zingat/andversion/dev/art/andversionLogo.png)


<p align="start">
  <a target="_blank" href="https://android-arsenal.com/api?level=8"><img src="https://img.shields.io/badge/API-14%2B-orange.svg"></a>

</p>
This is the library that checks updates on Google Play according to json file from url address.
Force user to update application and show info for new version.

> This library uses [Material Dialog Library](https://github.com/afollestad/material-dialogs).
Thank you to [Aidan Follestad](https://github.com/afollestad)

# GRADLE DEPENDENCY
The minimum API level supported by this library is API 14.
Add the dependency to your `build.gradle`:

```Gradle
dependencies {
    compile 'com.zingat:andversion:1.0.0'
}
```

# USAGE
Add **INTERNET** and **ACCESS_NETWORK_STATE** permissions to your app's Manifest:
```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

### SAMPLE JSON FILE
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
**CurrentVersion :** The version code on Google Play. The value should be integer.

**MinVersion :** The lowest version code in build gradle that you want to support. The value should be integer.

**WhatsNew :** The list of new features to show to the user. The values should be also json object. It allows you to present new features of your application to user in different languages.
The key of inner object should be **locale languge code** for Android and the value of inner object shoul be **string array**. For language codes glance the list in this addres:
https://stackoverflow.com/questions/7973023/what-is-the-list-of-supported-languages-locales-on-android

### ANDVERSION IMPLEMENTATION

There are two way to implement AndVersion. You can check update and news separately using two methods or you can check all of them using a single method.

#### INITIALIZE

To initialize write Andversion write the following codes in activity or fragment where you want before use Andversion.
```java
@Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        AndVersion.getInstance()
                  .setActivity( this )
                  .setUri( ANDVERSION_URL );

}
```
If you are in fragment call setActivity( getActivity ) instead of setActivity( this ).

**Andversion_url** means the url where you keep the JSON file, like http://andversion.com/sample/demoIOS.json.

**Note :** You do not have to use setUri() methods every time when you initialize. It is enough to call once.

#### CALLING METHODS SEPARATELY

**checkForceUpdate() :** This method controls that user version code is smaller than minimum version in JSON file.

If yes the force update dialog will be displayed. If no or there was an error onCompleted method will be called.

```java
@Override
    protected void onResume() {
        super.onResume();

        AndVersion.getInstance().checkForceUpdate( new OnCompletedListener() {
            @Override
            public void onCompleted() {
                // The process what you want to do after check update.
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
#### Sample Screenshot for force update dialog.
![Screenshots](https://raw.githubusercontent.com/zingat/andversion/dev/art/forceUpdateShowcase.png)

**checkNews() :** This method is used for showing information dialog after update. Information dialog will be displayed only once after every update.


```java
@Override
    protected void onResume() {
        super.onResume();

        AndVersion.getInstance()
                .checkNews();

}
```
The suggestion is to call this method in main screen.

#### Sample Screenshot for whats new dialog.
![Screenshots](https://raw.githubusercontent.com/zingat/andversion/dev/art/whatsNewShowcase.png)

#### CALLING A SINGLE METHOD

**checkUpdate() :** First of all you also initialze Andversion before call this method. 
This method controls that user should update application. 

If yes, force update dialog will be displayed. If no, method will check is there any information about news. 
If there are some news information dialog will be displayed if not onCompleted method will be called.

**Note :** When any error occurs onCompleted method will be called. 

```java
@Override
    protected void onResume() {
        super.onResume();
        
         AndVersion.getInstance()
                .checkUpdate( new OnCompletedListener() {
                    @Override
                    public void onCompleted() {
                       // The process what you want to do after check update.
                    }
                } );

}
```
The suggestion is to call this method in splash screen. 

#### CUSTOMIZE TITLE AND BUTTONS TEXT

To customize dialog title and buttons text add the following xml codes in your app strings.xml.

```xml
<string name="andversion_forceupdate_title">What's New Waitin For You!</string>
<string name="andversion_whatsnew_title">News In This Version!</string>
<string name="andversion_update">Update</string>
<string name="andversion_exit_app">Exit</string>
<string name="andversion_ok">Ok</string>
```

<img align="right" src="https://raw.githubusercontent.com/zingat/andversion/dev/art/zingatLogo.png" width="320">