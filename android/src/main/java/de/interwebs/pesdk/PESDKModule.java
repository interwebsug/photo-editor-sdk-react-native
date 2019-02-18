/**
 * PhotoEditorSDK ReactNative Module
 * <p>
 * Created 08/2017 by Interwebs UG (haftungsbeschränkt)
 *
 * @author Michel Albers <m.albers@interwebs-ug.de>
 * @license The Unlincese (unlincese.org)
 */

package de.interwebs.pesdk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ly.img.android.pesdk.assets.filter.basic.FilterPackBasic;
import ly.img.android.pesdk.backend.model.constant.Directory;
import ly.img.android.pesdk.backend.model.state.EditorLoadSettings;
import ly.img.android.pesdk.backend.model.state.EditorSaveSettings;
import ly.img.android.pesdk.backend.model.state.manager.SettingsList;
import ly.img.android.pesdk.ui.activity.CameraPreviewBuilder;
import ly.img.android.pesdk.ui.activity.ImgLyIntent;
import ly.img.android.pesdk.ui.activity.PhotoEditorBuilder;
import ly.img.android.pesdk.ui.model.state.UiConfigFilter;

public class PESDKModule extends ReactContextBaseJavaModule {

    // the answer to life the universe and everything
    static final int RESULT_CODE_PESDK = 42;

    // Promise for later use
    private Promise mPESDKPromise;

    // Error constants
    private static final String E_ACTIVITY_DOES_NOT_EXIST = "ACTIVITY_DOES_NOT_EXIST";
    private static final String E_PESDK_CANCELED = "USER_CANCELED_EDITING";

    // Features
    public static final String transformTool = "transformTool";
    public static final String filterTool = "filterTool";
    public static final String focusTool = "focusTool";
    public static final String adjustTool = "adjustTool";
    public static final String textTool = "textTool";
    public static final String stickerTool = "stickerTool";
    public static final String overlayTool = "overlayTool";
    public static final String brushTool = "brushTool";
    public static final String magic = "magic";

    // Config options
    public static final String backgroundColorCameraKey = "backgroundColor";
    public static final String backgroundColorEditorKey = "backgroundColorEditor";
    public static final String backgroundColorMenuEditorKey = "backgroundColorMenuEditor";
    public static final String cameraRollAllowedKey = "cameraRollAllowed";
    public static final String showFiltersInCameraKey = "showFiltersInCamera";

    // Listen for onActivityResult
    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case RESULT_CODE_PESDK: {
                    switch (resultCode) {
                        case Activity.RESULT_CANCELED:
                            mPESDKPromise.reject(E_PESDK_CANCELED, "Editor was cancelled");
                            break;
                        case Activity.RESULT_OK:
                            String resultPath = data.getStringExtra(ImgLyIntent.RESULT_IMAGE_PATH);
                            mPESDKPromise.resolve(resultPath);
                            break;
                    }
                    mPESDKPromise = null;
                    break;
                }
            }
        }
    };


    public PESDKModule(ReactApplicationContext context) {
        super(context);
        context.addActivityEventListener(mActivityEventListener);
    }

    // Config builder
    private SettingsList buildConfig(ReadableMap options, @Nullable ReadableArray features, @Nullable Uri imageUri) {
        SettingsList settingsList = new SettingsList();

        settingsList.getSettingsModel(EditorSaveSettings.class)
                .setExportDir(Directory.DCIM, "PESDK")
                .setExportPrefix("PESDK_")
                .setSavePolicy(EditorSaveSettings.SavePolicy.RETURN_ALWAYS_ONLY_OUTPUT);

        settingsList.getSettingsModel(UiConfigFilter.class).setFilterList(
                FilterPackBasic.getFilterPack()
        );

        if (imageUri != null) {
            settingsList.getSettingsModel(EditorLoadSettings.class)
                    .setImageSource(imageUri);
        }

        return settingsList;
    }

    @Override
    public String getName() {
        return "PESDK";
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        final Map<String, java.lang.Object> constants = new HashMap<String, Object>();
        constants.put("transformTool", transformTool);
        constants.put("filterTool", filterTool);
        constants.put("focusTool", focusTool);
        constants.put("adjustTool", adjustTool);
        constants.put("textTool", textTool);
        constants.put("stickerTool", stickerTool);
        constants.put("overlayTool", overlayTool);
        constants.put("brushTool", brushTool);
        constants.put("magic", magic);
        constants.put("backgroundColorCameraKey", backgroundColorCameraKey);
        constants.put("backgroundColorEditorKey", backgroundColorEditorKey);
        constants.put("backgroundColorMenuEditorKey", backgroundColorMenuEditorKey);
        constants.put("cameraRollAllowedKey", cameraRollAllowedKey);
        constants.put("showFiltersInCameraKey", showFiltersInCameraKey);

        return constants;
    }

    @ReactMethod
    public void openEditor(@NonNull String image, ReadableArray features, ReadableMap options, final Promise promise) {
        if (getCurrentActivity() == null) {
            promise.reject(E_ACTIVITY_DOES_NOT_EXIST, "Activity does not exist");
        } else {
            mPESDKPromise = promise;

            SettingsList settingsList = buildConfig(options, features, Uri.fromFile(new File(image)));

            new PhotoEditorBuilder(getCurrentActivity())
                    .setSettingsList(settingsList)
                    .startActivityForResult(getCurrentActivity(), RESULT_CODE_PESDK);
        }
    }

    @ReactMethod
    public void openCamera(ReadableArray features, ReadableMap options, final Promise promise) {
        if (getCurrentActivity() == null) {
            promise.reject(E_ACTIVITY_DOES_NOT_EXIST, "Activity does not exist");
        }
        else {
            mPESDKPromise = promise;

            SettingsList settingsList = buildConfig(options, features, null);

            new CameraPreviewBuilder(getCurrentActivity())
                    .setSettingsList(settingsList)
                    .startActivityForResult(getCurrentActivity(), RESULT_CODE_PESDK);
        }
    }
}
