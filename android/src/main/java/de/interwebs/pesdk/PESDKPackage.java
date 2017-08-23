/**
 * PhotoEditorSDK ReactNative Module
 *
 * Created 08/2017 by Interwebs UG (haftungsbeschränkt)
 * @author Michel Albers <m.albers@interwebs-ug.de>
 * @license Unlicense (unlicense.org)
 *
 */

package de.interwebs.pesdk;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.*;

public class PESDKPackage implements ReactPackage {
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(new PESDKModule(reactContext));

        return modules;
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return emptyList();
    }
}
