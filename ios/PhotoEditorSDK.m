//
//  PhotoEditorSDK.m
//  FantasticPost
//
//  Created by Michel Albers on 16.08.17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import "PhotoEditorSDK.h"
#import "React/RCTUtils.h"
#import "AVHexColor.h"

// Config options
NSString* const kBackgroundColorEditorKey = @"backgroundColorEditor";
NSString* const kBackgroundColorMenuEditorKey = @"backgroundColorMenuEditor";
NSString* const kBackgroundColorCameraKey = @"backgroundColorCamera";
NSString* const kCameraRollAllowedKey = @"cameraRowAllowed";
NSString* const kShowFiltersInCameraKey = @"showFiltersInCamera";

// Menu items
typedef enum {
    transformTool,
    filterTool,
    focusTool,
    adjustTool,
    textTool,
    stickerTool,
    overlayTool,
    brushTool,
    magic,
} FeatureType;

@interface PhotoEditorSDK ()

@property (strong, nonatomic) RCTPromiseResolveBlock resolver;
@property (strong, nonatomic) RCTPromiseRejectBlock rejecter;
@property (strong, nonatomic) PESDKPhotoEditViewController* editController;
@property (strong, nonatomic) PESDKCameraViewController* cameraController;


@end

@implementation PhotoEditorSDK
RCT_EXPORT_MODULE(PESDK);

static NSString *letters = @"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

+(NSString *) randomStringWithLength: (int) len {
    
    NSMutableString *randomString = [NSMutableString stringWithCapacity: len];
    
    for (int i=0; i<len; i++) {
        [randomString appendFormat: @"%C", [letters characterAtIndex: arc4random_uniform([letters length])]];
    }
    
    return randomString;
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

- (NSDictionary *)constantsToExport
{
    return @{
             @"backgroundColorCameraKey":       kBackgroundColorCameraKey,
             @"backgroundColorEditorKey":       kBackgroundColorEditorKey,
             @"backgroundColorMenuEditorKey":   kBackgroundColorMenuEditorKey,
             @"cameraRollAllowedKey":           kCameraRollAllowedKey,
             @"showFiltersInCameraKey":         kShowFiltersInCameraKey,
             @"transformTool":                  [NSNumber numberWithInt: transformTool],
             @"filterTool":                     [NSNumber numberWithInt: filterTool],
             @"focusTool":                      [NSNumber numberWithInt: focusTool],
             @"adjustTool":                     [NSNumber numberWithInt: adjustTool],
             @"textTool":                       [NSNumber numberWithInt: textTool],
             @"stickerTool":                    [NSNumber numberWithInt: stickerTool],
             @"overlayTool":                    [NSNumber numberWithInt: overlayTool],
             @"brushTool":                      [NSNumber numberWithInt: brushTool],
             @"magic":                          [NSNumber numberWithInt: magic]
    };
}

-(void)_openEditor: (UIImage *)image config: (PESDKConfiguration *)config features: (NSArray*)features resolve: (RCTPromiseResolveBlock)resolve reject: (RCTPromiseRejectBlock)reject {
    self.resolver = resolve;
    self.rejecter = reject;
    
    // Just an empty model
    PESDKPhotoEditModel* photoEditModel = [[PESDKPhotoEditModel alloc] init];
    
    // Build the menu items from the features array if present
    NSMutableArray<PESDKPhotoEditMenuItem *>* menuItems = [[NSMutableArray alloc] init];
    
    // Default features
    if (features == nil || [features count] == 0) {
        features = @[
          [NSNumber numberWithInt: transformTool],
          [NSNumber numberWithInt: filterTool],
          [NSNumber numberWithInt: focusTool],
          [NSNumber numberWithInt: adjustTool],
          [NSNumber numberWithInt: textTool],
          [NSNumber numberWithInt: stickerTool],
          [NSNumber numberWithInt: overlayTool],
          [NSNumber numberWithInt: brushTool],
          [NSNumber numberWithInt: magic]
        ];
    }
    
    [features enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        int feature = [obj intValue];
        switch (feature) {
            case transformTool: {
                PESDKToolMenuItem* menuItem = [PESDKToolMenuItem createTransformToolItem];
                PESDKPhotoEditMenuItem* editMenuItem = [[PESDKPhotoEditMenuItem alloc] initWithToolMenuItem:menuItem];
                [menuItems addObject: editMenuItem];
                break;
            }
            case filterTool: {
                PESDKToolMenuItem* menuItem = [PESDKToolMenuItem createFilterToolItem];
                PESDKPhotoEditMenuItem* editMenuItem = [[PESDKPhotoEditMenuItem alloc] initWithToolMenuItem:menuItem];
                [menuItems addObject: editMenuItem];
                break;
            }
            case focusTool: {
                PESDKToolMenuItem* menuItem = [PESDKToolMenuItem createFocusToolItem];
                PESDKPhotoEditMenuItem* editMenuItem = [[PESDKPhotoEditMenuItem alloc] initWithToolMenuItem:menuItem];
                [menuItems addObject: editMenuItem];
                break;
            }
            case adjustTool: {
                PESDKToolMenuItem* menuItem = [PESDKToolMenuItem createAdjustToolItem];
                PESDKPhotoEditMenuItem* editMenuItem = [[PESDKPhotoEditMenuItem alloc] initWithToolMenuItem:menuItem];
                [menuItems addObject: editMenuItem];
                break;
            }
            case textTool: {
                PESDKToolMenuItem* menuItem = [PESDKToolMenuItem createTextToolItem];
                PESDKPhotoEditMenuItem* editMenuItem = [[PESDKPhotoEditMenuItem alloc] initWithToolMenuItem:menuItem];
                [menuItems addObject: editMenuItem];
                break;
            }
            case stickerTool: {
                PESDKToolMenuItem* menuItem = [PESDKToolMenuItem createStickerToolItem];
                PESDKPhotoEditMenuItem* editMenuItem = [[PESDKPhotoEditMenuItem alloc] initWithToolMenuItem:menuItem];
                [menuItems addObject: editMenuItem];
                break;
            }
            case overlayTool: {
                PESDKToolMenuItem* menuItem = [PESDKToolMenuItem createOverlayToolItem];
                PESDKPhotoEditMenuItem* editMenuItem = [[PESDKPhotoEditMenuItem alloc] initWithToolMenuItem:menuItem];
                [menuItems addObject: editMenuItem];
                break;
            }
            case brushTool: {
                PESDKToolMenuItem* menuItem = [PESDKToolMenuItem createBrushToolItem];
                PESDKPhotoEditMenuItem* editMenuItem = [[PESDKPhotoEditMenuItem alloc] initWithToolMenuItem:menuItem];
                [menuItems addObject: editMenuItem];
                break;
            }
            case magic: {
                PESDKActionMenuItem* menuItem = [PESDKActionMenuItem createMagicItem];
                PESDKPhotoEditMenuItem* editMenuItem = [[PESDKPhotoEditMenuItem alloc] initWithActionMenuItem:menuItem];
                [menuItems addObject: editMenuItem];
                break;
            }
            default:
                break;
        }
    }];
    
    
    self.editController = [[PESDKPhotoEditViewController alloc] initWithPhoto:image configuration:config menuItems:menuItems photoEditModel:photoEditModel];
    
    self.editController.delegate = self;
    UIViewController *currentViewController = RCTPresentedViewController();
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [currentViewController presentViewController:self.editController animated:YES completion:nil];
    });
}

-(PESDKConfiguration*)_buildConfig: (NSDictionary *)options {
    PESDKConfiguration* config = [[PESDKConfiguration alloc] initWithBuilder:^(PESDKConfigurationBuilder * builder) {
        [builder configurePhotoEditorViewController:^(PESDKPhotoEditViewControllerOptionsBuilder * b) {
            if ([options valueForKey:kBackgroundColorEditorKey]) {
                b.backgroundColor = [AVHexColor colorWithHexString: [options valueForKey:kBackgroundColorEditorKey]];
            }
            
            if ([options valueForKey:kBackgroundColorMenuEditorKey]) {
                b.menuBackgroundColor = [AVHexColor colorWithHexString: [options valueForKey:kBackgroundColorMenuEditorKey]];
            }
            
        }];
        
        [builder configureCameraViewController:^(PESDKCameraViewControllerOptionsBuilder * b) {
            if ([options valueForKey:kBackgroundColorCameraKey]) {
                b.backgroundColor = [AVHexColor colorWithHexString: (NSString*)[options valueForKey:kBackgroundColorCameraKey]];
            }
            
            if ([[options allKeys] containsObject:kCameraRollAllowedKey]) {
                b.showCameraRoll = [[options valueForKey:kCameraRollAllowedKey] boolValue];
            }
            
            if ([[options allKeys] containsObject: kShowFiltersInCameraKey]) {
                b.showFilters = [[options valueForKey:kShowFiltersInCameraKey] boolValue];
            }
            
            // TODO: Video recording not supported currently
            b.allowedRecordingModesAsNSNumbers = @[[NSNumber numberWithInteger:RecordingModePhoto]];
        }];
    }];
    
    return config;
}

RCT_EXPORT_METHOD(openEditor: (NSString*)path options: (NSArray *)features options: (NSDictionary*) options resolve: (RCTPromiseResolveBlock)resolve reject: (RCTPromiseRejectBlock)reject) {
    UIImage* image = [UIImage imageWithContentsOfFile: path];
    PESDKConfiguration* config = [self _buildConfig:options];
    [self _openEditor:image config:config features:features resolve:resolve reject:reject];
}

- (void)close {
    UIViewController *currentViewController = RCTPresentedViewController();
    [currentViewController dismissViewControllerAnimated:YES completion:nil];
}

RCT_EXPORT_METHOD(openCamera: (NSArray*) features options:(NSDictionary*) options resolve: (RCTPromiseResolveBlock)resolve reject: (RCTPromiseRejectBlock)reject) {
    __weak typeof(self) weakSelf = self;
    UIViewController *currentViewController = RCTPresentedViewController();
    PESDKConfiguration* config = [self _buildConfig:options];
    
    self.cameraController = [[PESDKCameraViewController alloc] initWithConfiguration:config];

    [self.cameraController.cameraController setupWithInitialRecordingMode:RecordingModePhoto error:nil];
    
    UISwipeGestureRecognizer* swipeDownRecognizer = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(close)];
    swipeDownRecognizer.direction = UISwipeGestureRecognizerDirectionDown;
    
    [self.cameraController.view addGestureRecognizer:swipeDownRecognizer];
    [self.cameraController setCompletionBlock:^(UIImage * image, NSURL * _) {
        [currentViewController dismissViewControllerAnimated:YES completion:^{
            [weakSelf _openEditor:image config:config features:features resolve:resolve reject:reject];
        }];
    }];
    
    [currentViewController presentViewController:self.cameraController animated:YES completion:nil];
}

-(void)photoEditViewControllerDidCancel:(PESDKPhotoEditViewController *)photoEditViewController {
    if (self.rejecter != nil) {
        self.rejecter(@"DID_CANCEL", @"User did cancel the editor", nil);
        self.rejecter = nil;
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.editController.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
        });
    }
}

-(void)photoEditViewControllerDidFailToGeneratePhoto:(PESDKPhotoEditViewController *)photoEditViewController {
    if (self.rejecter != nil) {
        self.rejecter(@"DID_FAIL_TO_GENERATE_PHOTO", @"Photo generation failed", nil);
        self.rejecter = nil;
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.editController.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
        });
        
    }
}

-(void)photoEditViewController:(PESDKPhotoEditViewController *)photoEditViewController didSaveImage:(UIImage *)image imageAsData:(NSData *)data {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,
                                                         NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *randomPath = [PhotoEditorSDK randomStringWithLength:10];
    NSString* path = [documentsDirectory stringByAppendingPathComponent:
                      [randomPath stringByAppendingString:@".jpg"] ];
    
    [data writeToFile:path atomically:YES];
    self.resolver(path);
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.editController.presentingViewController dismissViewControllerAnimated:YES completion:NULL];
    });
    
}

@end
