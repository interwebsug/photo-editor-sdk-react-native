# React Native PhotoEditorSDK Implementation
This project aims to be a complete implementation of the PhotoEditor SDK (https://www.photoeditorsdk.com/) in React Native. Currently only android is supported but iOS will follow shortly.

## Features

### You can:

- Open the Editor with a given image path
- Specify which editor features should be enabled
- Retrieve the edited image path afterwards
- Use TypeScript since a declaration file exists

### You can't (currently, but on todo list)

- Open the PESDK Camera (will follow shortly)
- Specify the path of the exported images
- Add own stickers
- Use iOS

## Installation

### General

Install the npm package via ```npm i photo-editor-sdk-react-native --save```

### Android

### Step 0 - Follow the official installation guide
In order to have the base module running, follow the installation guide at https://docs.photoeditorsdk.com/guides/android/v4/introduction/getting_started

Don't forget the ```PESDK.init()``` call to your ```MainApplication.java```.

#### Step 1 - Modify ```settings.gradle```
Add the following lines to your ```settings.gradle```:

    include ':photo-editor-sdk-react-native'
    project(':photo-editor-sdk-react-native').projectDir = new File(rootProject.projectDir, '../node_modules/photo-editor-sdk-react-native/android')

#### Step 2 - Modify ```build.gradle``` file (app)
Add the following lines to your ```build.gradle``` file:

    dependencies {
        ....
        compile project(':react-native-fbsdk')
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
iOS is still a TODO :( Here is a funny cat gif for you anyway.

![funny cat gif](https://media.giphy.com/media/IZl79Ik04xaUg/giphy.gif)

## Usage

### Open the image editor

#### Step 1 - Import the module
At the top of your .js or .ts file add ```import PESDK from 'photo-editor-sdk-react-native';```

#### Step 2 - Open the editor
Supply an image path to the editor and define the features you need. Available editor features are represented as string constants:

- Transformation (```PESDK.TRANSFORM_TOOL```)
- Filters (```PESDK.FILTER_TOOL```)
- Focus points (```PESDK.FOCUS_TOOL```)
- Color Adjustment (```PESDK.ADJUST_TOOL```)
- Text (```PESDK.TEXT_TOOL```)
- Stickers (```PESDK.STICKER_TOOL```)
- Overlays (```PESDK.OVERLAY_TOOL```)
- Brush (```PESDK.BRUSH_TOOL```)
- Divider (vertical line between buttons)  (```PESDK.DIVIDER```)

If you want to open the editor with all features you could for example achieve this by:

    PESDK.openEditor('path-to-your-image', [
      PESDK.TRANSFORM_TOOL,
      PESDK.DIVIDER,
      PESDK.FILTER_TOOL,
      PESDK.FOCUS_TOOL,
      PESDK.ADJUST_TOOL,
      PESDK.TEXT_TOOL,
      PESDK.STICKER_TOOL,
      PESDK.OVERLAY_TOOL,
      PESDK.BRUSH_TOOL
    ])
    .then((imagePath) => console.log(imagePath))
    .catch((err) => console.error(err));

### Contribution
Contribution is always welcome in the form of pull requests :) 

### License
Unlicense: http://unlicense.org/ - Just do what you want with it ;)
