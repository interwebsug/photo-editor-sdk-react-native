# Photo Editor SDK React Native
This project aims to be a complete implementation of the PhotoEditor SDK (https://www.photoeditorsdk.com/) in React Native for iOS and Android. 

**Please note: a PhotoEditorSDK license is needed for usage: https://www.photoeditorsdk.com/** Unfortunetaly pricing is only available after asking for a quote. But it's affordable ;)

## Features

### You can:
- Open the PESDK Camera 
- Open the Editor with a given image path
- Use iOS & Android
- Specify which editor features should be enabled
- Retrieve the edited image path afterwards
- Use TypeScript since a declaration file exists

### You can't (currently, but on todo list)

- Specify the path of the exported images
- Add own stickers

## Installation

### General

Install the npm package via ```npm i photo-editor-sdk-react-native --save``` or ```yarn add photo-editor-sdk-react-native --save```.

### Android

#### Step 0 - Follow the official installation guide
In order to have the base module running, follow the installation guide at https://docs.photoeditorsdk.com/guides/android/v5/introduction/overview

Don't forget the ```PESDK.init()``` call to your ```MainApplication.java```.

#### Step 1 - Modify ```settings.gradle```
Add the following lines to your ```settings.gradle```:

    include ':photo-editor-sdk-react-native'
    project(':photo-editor-sdk-react-native').projectDir = new File(rootProject.projectDir, '../node_modules/photo-editor-sdk-react-native/android')

#### Step 2 - Modify ```build.gradle``` file (app)
Add the following lines to your ```build.gradle``` file:

    dependencies {
        ....
        compile project(':photo-editor-sdk-react-native')
    }

### Step 3 - Modify ```getPackages()``` inside ```MainApplication.java```
Add the PESDKPackage to the ```getPackages``` function inside ```MainApplication.java```

    @Override
    protected List<ReactPackage> getPackages() {
        return Arrays.<ReactPackage>asList(
            ...
            new PESDKPackage(),
            ...
        );
    }
    
**That's it - you're done!**

### iOS
#### Step 0 - Follow the official installation guide

In order to have the base module running, follow the installation guide at https://docs.photoeditorsdk.com/guides/ios/v8/introduction/overview but be sure to _NOT_ use ```use_frameworks!```.

#### Step 1

Add photo-editor-sdk-react-native to your Podfile:

    pod 'photo-editor-sdk-react-native', :path => '../../photo-editor-sdk-react-native'

and run ```pod install``` afterwards.

#### Step 2

Add the privacy keys to your ```Info.plist``` file or otherwise the app will crash when trying to use the camera or accessing the media library:

    <key>NSCameraUsageDescription</key>
    <string>Take photos</string>
    <key>NSPhotoLibraryUsageDescription</key>
    <string>Choose Photos from library</string>


#### Step 3

Don't forget the ```[PESDK unlockWithLicenseAt:[[NSBundle mainBundle] URLForResource:@"<<YOUR LICENSE FILE>>" withExtension:nil]];``` call to your ```AppDelegate.m```. (or equivalent in Swift)


**That's it - you're done!**

## Usage

### Configuration options

#### Features

Available editor features are represented as constants:

- Transformation (```PESDK.transformTool```)
- Filters (```PESDK.filterTool```)
- Focus points (```PESDK.focusTool```)
- Color Adjustment (```PESDK.adjustTool```)
- Text (```PESDK.textTool```)
- Stickers (```PESDK.stickerTool```)
- Overlays (```PESDK.overlayTool```)
- Brush (```PESDK.brushTool```)
- Magic (```PESDK.magic```) **not available on Android**

see how to use them below.

#### Configuration

Avaiable configuration options are also represented as constant keys. The config has to be supplied as an array. See example below.

- Background color for camera (```PESDK.backgroundColorCameraKey```)
- Background color for editor (```PESDK.backgroundColorEditoKey```)
- Background color for the editor menu (```PESDK.backgroundColorMenuEditorKey```)
- Camera roll allowed (```PESDK.cameraRollAllowedKey```)
- Show filters in camera (```PESDK.showFiltersInCameraKey```)

**Configuration will be ignored on android currently.**

### Import the module

At the top of your .js or .ts file add ```import PESDK from 'photo-editor-sdk-react-native';```

### Open the image editor

Supply an image path to the editor and define the features you need:

    PESDK.openEditor(
         'PATH-TO-YOUR-IMAGE',
         [features],
         {config}
    )
    .then((imagePath) => /* .. do something with it .. */)
    .catch((err) => /* .. handle the error .. */);

If you want to open the editor with all features you could for example achieve this by:

    PESDK.openEditor('path-to-your-image', [
      PESDK.transformTool,
      PESDK.filterTool,
      PESDK.focusTool,
      PESDK.adjustTool,
      PESDK.textTool,
      PESDK.stickerTool,
      PESDK.overlayTool,
      PESDK.brushTool,
      PESDK.magic
    ], {
      [PESDK.backgroundColorCameraKey]: '#000',
      [PESDK.backgroundColorEditorKey]: '#000',
      [PESDK.backgroundColorMenuEditorKey]: '#000',
      [PESDK.cameraRollAllowedKey]: false,
      [PESDK.showFiltersInCameraKey]: true,
    })
    .then((imagePath) => console.log(imagePath))
    .catch((err) => console.error(err));

**Note for iOS users: Since Apple is a bit restrictive on file system access you may have to fiddle with RNFetchBlob (https://github.com/wkh237/react-native-fetch-blob) to directly open the editor with an image filepath.**

### Open the camera view

Open the camera and after choosing or taking an image enhance it with the editor: 

    PESDK.openCamera(
      [features],
      {config}
    )
    .then((imagePath) => /* .. do something with it .. */)
    .catch((err) => /* .. handle the error .. */);

Example with all options available used:

    PESDK.openCamera([
      PESDK.transformTool,
      PESDK.filterTool,
      PESDK.focusTool,
      PESDK.adjustTool,
      PESDK.textTool,
      PESDK.stickerTool,
      PESDK.overlayTool,
      PESDK.brushTool,
      PESDK.magic
    ], {
      [PESDK.backgroundColorCameraKey]: '#000',
      [PESDK.backgroundColorEditorKey]: '#000',
      [PESDK.backgroundColorMenuEditorKey]: '#000',
      [PESDK.cameraRollAllowedKey]: false,
      [PESDK.showFiltersInCameraKey]: true,
    })
    .then((imagePath) => console.log(imagePath))
    .catch((err) => console.error(err));

**Note for iOS: There is no back button implemented so I added a swipe down gesture recognizer for closing the camera. On Android the hardware back button will work.**

### Contribution
Contribution is always welcome via pull requests :) 

### License
Unlicense: http://unlicense.org/ - Just do what you want with it.
