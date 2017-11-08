/*
 * Copyright (c) 2016-present Shanghai Droi Technology Co., Ltd.
 * All rights reserved.
 */

#import <Foundation/Foundation.h>
#import "DroiObject.h"

/// Auth type
typedef NS_ENUM(int, AuthType) {
    /// QQ
    AUTH_TYPE_QQ = 0,
    /// Sina
    AUTH_TYPE_SINA = 1,
    /// Weixin
    AUTH_TYPE_WEIXIN = 2
    // 3 is reserved for Facebook, do not use
    // 4 is reserved for DroiAccount, do not use
};

/**
 * DroiOAuthProvider. The base class to create OAuth provider.
 */
@interface DroiOAuthProvider : NSObject
/**
 * Create AuthProvider for 3rd party login.
 *
 * @param type AuthType
 * @return return DroiOAuthProvider.
 */
+ (instancetype) providerWithType:(AuthType) type;

/**
 * Create AuthProvider for 3rd party login.
 *
 * @param type AuthType
 * @param scope Permission scope
 * @return return DroiOAuthProvider.
 */
+ (instancetype) providerWithType:(AuthType) type scope:(NSString*) scope;


/**
 * Call this function in AppDelegate.openUrl & APpDelegate.handleOpenUrl
 * @param url The URL resource to open.
 * @param sourceApplication The bundle ID of the app that is requesting your app to open the URL (url).
 * @param annotation A Property list supplied by the source app to communicate information to the receiving app.
 * @return YES for success.
 */
+ (BOOL) handleOpenUrl:(NSURL*) url sourceApplication:(NSString*) sourceApplication annotation:(id) annotation;

/**
 * When you change OAuth AppId/SecureKey in Droi Developer Web platform, need to call this api to fetch OAuth data.
 * @param callback Callback
 */
+ (BOOL) fetchOAuthKeysInBackground:(DroiObjectCallback) callback;

/**
 * When you change OAuth AppId/SecureKey in Droi Developer Web platform, need to call this api to fetch OAuth data.
 */
+ (DroiError*) fetchOAuthKeys;

/**
 * Internal use.
 * @param type Type
 * @param clazz Clazz
 */
+ (BOOL) registerProviderType:(AuthType) type class:(Class) clazz;

/**
 * Internal use.
 * @param url The URL resource to open.
 * @param sourceApplication The bundle ID of the app that is requesting your app to open the URL (url).
 * @param annotation A Property list supplied by the source app to communicate information to the receiving app.
 * @return YES for success. 
 */
- (BOOL) handleOpenUrl:(NSURL*) url sourceApplication:(NSString*) sourceApplication annotation:(id) annotation;
/**
 * User id. Please assign user id when unbinding OAuth.
 */
@property NSString* userId;
/**
 * User password. Please assign user password when unbinding OAuth.
 */
@property NSString* password;
/**
 * OAuth account unique id
 */
@property (readonly) NSString* id;
/**
 * Weixin OAuth code.
 */
@property (readonly) NSString* code;
/**
 * OAuth account accessToken;
 */
@property (readonly) NSString* accessToken;
/**
 * OAuthProvider name
 */
@property (readonly, getter=getProviderName) NSString* name;
/**
 * OAuthProvider version
 */
@property (readonly, getter=getProviderVersion) NSString* providerVersion;
@end
