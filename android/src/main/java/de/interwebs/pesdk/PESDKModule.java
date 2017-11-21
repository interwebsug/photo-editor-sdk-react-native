/**
 * PhotoEditorSDK ReactNative Module
 *
 * Created 08/2017 by Interwebs UG (haftungsbeschränkt)
 * @author Michel Albers <m.albers@interwebs-ug.de>
 * @license The Unlincese (unlincese.org)
 *
 */

package de.interwebs.pesdk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ly.img.android.sdk.models.config.Divider;
import ly.img.android.sdk.models.config.interfaces.ToolConfigInterface;
import ly.img.android.sdk.models.constant.Directory;
import ly.img.android.sdk.models.state.CameraSettings;
import ly.img.android.sdk.models.state.EditorLoadSettings;
import ly.img.android.sdk.models.state.EditorMenuState;
import ly.img.android.sdk.models.state.EditorSaveSettings;
import ly.img.android.sdk.models.state.PESDKConfig;
import ly.img.android.sdk.models.state.manager.SettingsList;
import ly.img.android.sdk.tools.BrushEditorTool;
import ly.img.android.sdk.tools.ColorAdjustmentTool;
import ly.img.android.sdk.tools.FilterEditorTool;
import ly.img.android.sdk.tools.FocusEditorTool;
import ly.img.android.sdk.tools.OverlayEditorTool;
import ly.img.android.sdk.tools.StickerEditorTool;
import ly.img.android.sdk.tools.TextEditorTool;
import ly.img.android.sdk.tools.TransformEditorTool;
import ly.img.android.ui.activities.CameraPreviewBuilder;
import ly.img.android.ui.activities.ImgLyIntent;
import ly.img.android.ui.activities.PhotoEditorBuilder;

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
    private SettingsList buildConfig(ReadableMap options, @Nullable ReadableArray features, @Nullable String imagePath) {
        SettingsList settingsList = new SettingsList();
        settingsList
                .getSettingsModel(EditorLoadSettings.class)
                .setImageSourcePath(imagePath)
                .getSettingsModel(EditorSaveSettings.class)
                // TODO: Make export directory configurable
                .setExportDir(Directory.DCIM, "PESDK")
                .setExportPrefix("PESDK_")
                .setSavePolicy(
                        EditorSaveSettings.SavePolicy.RETURN_ALWAYS_ONLY_OUTPUT
                );



                // TODO: Config options in PESDK v5 are limited compared to iOS (or I didn't find them)

        PESDKConfig config = settingsList.getConfig();



        ArrayList<ToolConfigInterface> tools = new ArrayList<>();
        ArrayList featureList;

        if (features == null || features.size() == 0) {
            featureList = new ArrayList();
            featureList.add(transformTool);
            featureList.add(filterTool);
            featureList.add(focusTool);
            featureList.add(adjustTool);
            featureList.add(textTool);
            featureList.add(stickerTool);
            featureList.add(overlayTool);
            featureList.add(brushTool);
            featureList.add(magic);
        } else {
            featureList = features.toArrayList();
        }



        for (Object f: featureList) {
            String feature = f.toString();
            switch (feature) {
                case transformTool:
                    tools.add(new TransformEditorTool(R.string.imgly_tool_name_crop, R.drawable.imgly_icon_tool_transform));
                    break;
                case filterTool:
                    tools.add(new FilterEditorTool(R.string.imgly_tool_name_filter, R.drawable.imgly_icon_tool_filters));
                    break;
                case focusTool:
                    tools.add(new FocusEditorTool(R.string.imgly_tool_name_focus, R.drawable.imgly_icon_tool_focus));
                    break;
                case adjustTool:
                    tools.add(new ColorAdjustmentTool(R.string.imgly_tool_name_adjust, R.drawable.imgly_icon_tool_adjust));
                    break;
                case textTool:
                    tools.add(new TextEditorTool(R.string.imgly_tool_name_text, R.drawable.imgly_icon_tool_text));
                    break;
                case stickerTool:
                    tools.add(new StickerEditorTool(R.string.imgly_tool_name_sticker, R.drawable.imgly_icon_tool_sticker));
                    break;
                case overlayTool:
                    tools.add(new OverlayEditorTool(R.string.imgly_tool_name_overlay, R.drawable.imgly_icon_tool_overlay));
                    break;
                case brushTool:
                    tools.add(new BrushEditorTool(R.string.imgly_tool_name_brush, R.drawable.imgly_icon_tool_brush));
                    break;
                case magic:
                    // No magic tool on android
                    break;
            }
        }

        config.setTools(tools);

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

            SettingsList settingsList = buildConfig(options, features, image.toString());

            new PhotoEditorBuilder(getCurrentActivity())
                    .setSettingsList(settingsList)
                    .startActivityForResult(getCurrentActivity(), RESULT_CODE_PESDK);
        }
    }

    @ReactMethod
    public void openCamera(ReadableArray features, ReadableMap options, final Promise promise) {
        if (getCurrentActivity() == null) {
            promise.reject(E_ACTIVITY_DOES_NOT_EXIST, "Activity does not exist");
        } else {
            mPESDKPromise = promise;

            SettingsList settingsList = buildConfig(options, features, null);

            new CameraPreviewBuilder(getCurrentActivity())
                    .setSettingsList(settingsList)
                    .startActivityForResult(getCurrentActivity(), RESULT_CODE_PESDK);
        }
    }

}
