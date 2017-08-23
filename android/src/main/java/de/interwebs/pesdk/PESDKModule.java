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
import android.support.annotation.NonNull;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import ly.img.android.sdk.models.config.Divider;
import ly.img.android.sdk.models.config.interfaces.ToolConfigInterface;
import ly.img.android.sdk.models.constant.Directory;
import ly.img.android.sdk.models.state.EditorLoadSettings;
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

    // Tools for configuartion
    private static final String TRANSFORM_TOOL_KEY = "TRANSFORM_TOOL";
    private static final String FILTER_TOOL_KEY = "FILTER_TOOL";
    private static final String FOCUS_TOOL_KEY = "FOCUS_TOOL";
    private static final String ADJUST_TOOL_KEY = "ADJUST_TOOL";
    private static final String TEXT_TOOL_KEY = "TEXT_TOOL";
    private static final String STICKER_TOOL_KEY = "STICKER_TOOL";
    private static final String OVERLAY_TOOL_KEY = "OVERLAY_TOOL";
    private static final String BRUSH_TOOL_KEY = "BRUSH_TOOL";
    private static final String DIVIDER_KEY = "DIVIDER";


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

    @Override
    public String getName() {
        return "PESDK";
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<String, Object>();
        constants.put(TRANSFORM_TOOL_KEY, "transform");
        constants.put(FILTER_TOOL_KEY, "filter");
        constants.put(FOCUS_TOOL_KEY, "focus");
        constants.put(ADJUST_TOOL_KEY, "adjust");
        constants.put(TEXT_TOOL_KEY, "text");
        constants.put(STICKER_TOOL_KEY, "sticker");
        constants.put(OVERLAY_TOOL_KEY, "overlay");
        constants.put(BRUSH_TOOL_KEY, "brush");
        constants.put(DIVIDER_KEY, "divider");
        return constants;
    }

    @ReactMethod
    public void openEditor(@NonNull String image, ReadableArray features, final Promise promise) {
        if (getCurrentActivity() == null) {
           promise.reject(E_ACTIVITY_DOES_NOT_EXIST, "Activity does not exist");
        } else {
            mPESDKPromise = promise;
            SettingsList settingsList = new SettingsList();
            settingsList
                    .getSettingsModel(EditorLoadSettings.class)
                    .setImageSourcePath(image)

                    .getSettingsModel(EditorSaveSettings.class)
                    // TODO: Make configurable
                    .setExportDir(Directory.DCIM, "PESDK")
                    .setExportPrefix("PESDK_")
                    .setSavePolicy(
                            EditorSaveSettings.SavePolicy.RETURN_ALWAYS_ONLY_OUTPUT
                    );

            PESDKConfig config = settingsList.getConfig();
            ArrayList<ToolConfigInterface> tools = new ArrayList<>();

            for (Object f: features.toArrayList()) {
               String feature = f.toString();
               switch (feature) {
                   case "transform":
                       tools.add(new TransformEditorTool(R.string.imgly_tool_name_crop, R.drawable.imgly_icon_tool_transform));
                       break;
                   case "filter":
                       tools.add(new FilterEditorTool(R.string.imgly_tool_name_filter, R.drawable.imgly_icon_tool_filters));
                       break;
                   case "focus":
                       tools.add(new FocusEditorTool(R.string.imgly_tool_name_focus, R.drawable.imgly_icon_tool_focus));
                       break;
                   case "adjust":
                       tools.add(new ColorAdjustmentTool(R.string.imgly_tool_name_adjust, R.drawable.imgly_icon_tool_adjust));
                       break;
                   case "text":
                       tools.add(new TextEditorTool(R.string.imgly_tool_name_text, R.drawable.imgly_icon_tool_text));
                       break;
                   case "sticker":
                       tools.add(new StickerEditorTool(R.string.imgly_tool_name_sticker, R.drawable.imgly_icon_tool_sticker));
                       break;
                   case "overlay":
                       tools.add(new OverlayEditorTool(R.string.imgly_tool_name_overlay, R.drawable.imgly_icon_tool_overlay));
                       break;
                   case "brush":
                       tools.add(new BrushEditorTool(R.string.imgly_tool_name_brush, R.drawable.imgly_icon_tool_brush));
                       break;
                   case "divider":
                       tools.add(new Divider());
                       break;
               }
            }

            config.setTools(tools);

            new PhotoEditorBuilder(getCurrentActivity())
                    .setSettingsList(settingsList)
                    .startActivityForResult(getCurrentActivity(), RESULT_CODE_PESDK);
        }
    }

}
