# WeChat Login Demo

The following is a demo of how to generate a token by using the WeChat SDK directly in android.
This can be a surprisingly difficult task as there is very little documentation and code examples
online. Much of the documentation around WeChat is in Chinese which makes debugging very difficult
if you run into any issues. WeChat also never respond if you have a bad configuration
leaving very little to Google.

This is a simplified demo just for login. The following is a more comprehensive demo of most (all) 
weChat features that really helped me figure out login: https://github.com/cihm/WeChatDemo

Link to an article I wrote about the experience: http://aaronbruckner.com/articleWeChat.html

# Create a WeChat App

To use WeChat you must be a registered developer. Go to https://open.weixin.qq.com. I will not
document this as I did not personally go through the process. You will have to create a mobile 
application which requires you to fill out some details about the application you are creating
that will interact with WeChat. From reading online it seems you can get blocked here if you
don't have a Chinese phone number to send a verification code to.

Once this is submitted and accepted, you will have a registered WeChat mobile application with
a WeChat App Id. The app Id is important as it's how WeChat identifies the application requesting
authorization.

# Import SDK

I couldn't find a gradle import for the WeChat SDK. Their official site gives instructions on how
to import their SDK into the libs folder of your application. http://dev.wechat.com/wechatapi/installguide

# Configure WeChat Signature/Package name

You must provide WeChat with a valid package name and signature for the development application
you will be using to invoke WeChat via the SDK. This can be set in the development portal at
https://open.weixin.qq.com. For the package name, use the package name from your manifest.

```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.company.your.package.name">

    ...

</manifest>
```

For the signature, use the MD5 hash of the key used to sign your published APK. The easiest way to do this is
to setup a signature to always use for your debug/release builds. If you have a java keystore holding a signing
key within your project, you can add the following to your app gradle file to sign your debug release with 
a constant signature:

```
android {
    ...
    // Keystore located in root project folder. Google can show many examples on how to generate keystores with signatures
    
    signingConfigs {
        debug {
            storeFile file('keystore.jks')
            storePassword 'password'
            keyAlias 'weChatDebug'
            keyPassword 'password'
        }
    }
    ...
}
```

If you do not use a set key for signing debug builds, android studio automatically signs your dev APK for you.
To determine what MD5 signature to provide to WeChat, I found this article useful: http://blog.sanuker.com/?p=691.
It holds a link to a Chinese app that pulls your MD5 hash for a provided application: 
https://github.com/mike623/cordova-WeChat-meteor-sharing/blob/master/README.md

**Warning** This is a random link I found on the internet, no promises as I have no involvement with the software.

If you use the application, when you run it, provide your package name in the first text field. Clicking the first 
button will show the MD5 for your app in the second text field. Paste this to WeChat.

### Converting Code to Access Token

When you finally get a token from WeChat, it's a code you must exchange for an access token using your
app ID and secret. The secret can be found in the WeChat developer portal.
To complete the exchange, it's just a simple rest call to a WeChat API.

API Call: https://api.wechat.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
Additional info: http://admin.wechat.com/wiki/index.php?title=User_Profile_via_Web. Scroll down to "Obtain access token by the code".

### Pitfalls

1. WeChat seems to break if your package name differs from your applicationId. This is probably due to the reflection
used by WeChat to respond to your request. If your package name differs from what's set in WeChat, you'll transition
to WeChat when an auth attempt is made but you'll never get a response. If your applicationID differs from what's 
in WeChat, nothing at all will happen when you request an authorization. Basically you must not use applicationId.
2. Package name can be mixed case but what's saved in WeChat must **exactly** match what's in your application.
3. The Signature hash should only be alpha numeric. Do no include other symbols like ":"
4. You must have a validated WeChat app on the device (use a real phone).
5. You must use the proper project structure. If your package name is com.test.app, you must place your activity for handling WeChat responses at com.test.app.wxapi.WXEntryActivity.
6. You must register before attempting to get a token.
7. Be careful with minified code (Proguard). There are articles online that mention minified code can mess up WeChat communication.
8. You must export your WXEntryActivity in your manifest.

Once everything is lined up, there is very little code needed to generate a token. However if anything is incorrect,
you will likely never see any transition to WeChat, a failure to load WeChat if a transition occurs, or no response 
from WeChat after you give your application access.