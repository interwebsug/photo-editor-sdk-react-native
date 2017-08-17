//
//  PhotoEditorSDK.m
//  FantasticPost
//
//  Created by Michel Albers on 16.08.17.
//  Copyright © 2017 Facebook. All rights reserved.
//

#import "PhotoEditorSDK.h"
#import "React/RCTUtils.h"

@interface PhotoEditorSDK ()

@property (strong, nonatomic) RCTPromiseResolveBlock resolver;
@property (strong, nonatomic) RCTPromiseRejectBlock rejecter;

@end

@implementation PhotoEditorSDK
RCT_EXPORT_MODULE("PESDK");

static NSString *letters = @"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

+(NSString *) randomStringWithLength: (int) len {
  
  NSMutableString *randomString = [NSMutableString stringWithCapacity: len];
  
  for (int i=0; i<len; i++) {
    [randomString appendFormat: @"%C", [letters characterAtIndex: arc4random_uniform([letters length])]];
  }
  
  return randomString;
}

RCT_EXPORT_METHOD(openEditor: (NSString*)path features: (NSArray*) features resolve: (RCTPromiseResolveBlock)resolve reject: (RCTPromiseRejectBlock)reject) {
  self.resolver = resolve;
  self.rejecter = reject;
  
  PESDKToolbarController* controller = [PESDKToolbarController new];
  UIImage* image = [UIImage imageWithContentsOfFile: path];
  PESDKPhotoEditViewController* editController = [[PESDKPhotoEditViewController alloc] initWithPhoto: image];
  editController.delegate = self;
  UIViewController *currentViewController = RCTPresentedViewController();
  
  dispatch_async(dispatch_get_main_queue(), ^{
    [controller pushViewController:editController animated:NO completion:NULL];
    [currentViewController presentViewController:controller animated:YES completion:NULL];
  });
}

-(void)photoEditViewControllerDidCancel:(PESDKPhotoEditViewController *)photoEditViewController {
  if (self.rejecter != nil) {
    self.rejecter(@"DID_CANCEL", @"User did cancel the editor", nil);
    self.rejecter = nil;
  }
}

-(void)photoEditViewControllerDidFailToGeneratePhoto:(PESDKPhotoEditViewController *)photoEditViewController {
  if (self.rejecter != nil) {
    self.rejecter(@"DID_FAIL_TO_GENERATE_PHOTO", @"Photo generation failed", nil);
    self.rejecter = nil;
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
}

@end
