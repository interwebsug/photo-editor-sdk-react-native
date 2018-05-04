//
//  PhotoEditorSDK.h
//  FantasticPost
//
//  Created by Michel Albers on 16.08.17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "React/RCTBridgeModule.h"
#import <PhotoEditorSDK/PhotoEditorSDK-Swift.h>

@interface PhotoEditorSDK : NSObject <RCTBridgeModule, PESDKPhotoEditViewControllerDelegate>

@end
