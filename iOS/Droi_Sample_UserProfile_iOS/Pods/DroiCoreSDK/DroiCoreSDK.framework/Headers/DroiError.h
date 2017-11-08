/*
 * Copyright (c) 2016-present Shanghai Droi Technology Co., Ltd.
 * All rights reserved.
 */

#import <Foundation/Foundation.h>

/**
 * Droi client error code
 */
typedef NS_ENUM(int, DroiErrorCode) {
    // General group
    /// OK
    DROICODE_OK = 0,
    /// Unknown error
    DROICODE_UNKNOWN_ERROR = 1070000,
    /// Error
    DROICODE_ERROR = 1070001,
    /// User is not exists
    DROICODE_USER_NOT_EXISTS = 1070002,
    /// Password is not correct
    DROICODE_USER_PASSWORD_INCORRECT = 1070003,
    /// User is already exists
    DROICODE_USER_ALREADY_EXISTS = 1070004,
    /// Network is not available
    DROICODE_NETWORK_NOT_AVAILABLE = 1070005,
    /// User is not authorized
    DROICODE_USER_NOT_AUTHORIZED = 1070006,
    /// Server is not reachable
    DROICODE_SERVER_NOT_REACHABLE = 1070007,
    /// Error happened in Server side
    DROICODE_HTTP_SERVER_ERROR = 1070008,
    /// Service is not allowed
    DROICODE_SERVICE_NOT_ALLOWED = 1070009,
    /// Service is not found
    DROICODE_SERVICE_NOT_FOUND = 1070010,
    /// Internal server error
    DROICODE_INTERNAL_SERVER_ERROR = 1070011,
    /// Invalid parameters
    DROICODE_INVALID_PARAMETER = 1070012,
    /// No permission
    DROICODE_NO_PERMISSION = 1070013,
    /// User is in disable state
    DROICODE_USER_DISABLE = 1070014,
    /// Exceed max size
    DROICODE_EXCEED_MAX_SIZE = 1070015,
    /// File is not ready
    DROICODE_FILE_NOT_READY = 1070016,
    /// Core is not initialized
    DROICODE_CORE_NOT_INITIALIZED = 1070017,
    /// User canceled
    DROICODE_USER_CANCELED = 1070018,
    /// Bandwidth limit exceed
    DROICODE_BANDWIDTH_LIMIT_EXCEED = 1070019,
    
    
    // Not allowed call this function from mainthread = 1070004
    /// Uncorrected application id
    DROICODE_APPLICATION_ID_UNCORRECTED = 1070101,
    
    // Network group
    /// Time is uncorrect
    DROICODE_TIME_UNCORRECTED = 1070201,
    /// Timeout
    DROICODE_TIMEOUT = 1070202,
    
    // Users group
    /// Already logged in a valid user
    DROICODE_USER_ALREADY_LOGIN = 1070301,
    /// User contact had verified
    DROICODE_USER_CONTACT_HAD_VERIFIED = 1070302,
    /// User contact is empty
    DROICODE_USER_CONTACT_EMPTY = 1070303,
    /// The function is not allowed for current user
    DROICODE_USER_FUNCTION_NOT_ALLOWED = 1070304,
    
    // DroiObject / DroiQuery
    /// Read cache fail
    DROICODE_READ_CACHE_FILE_FAILED = 10700501,
    /// Upload file failed
    DROICODE_UPLOAD_FILE_FAILED = 1070502,
    
};

/**
 *  This class represents all error code.
 */
@interface DroiError : NSObject<NSCopying>

/**
 *  Error Code. See DroiErrorCode
 */
@property (readonly) DroiErrorCode code;

/**
 *  Ticket ID for this error code
 */
@property (readonly) NSString* ticket;

/**
 *  Check whether the error is OK
 */
@property (readonly) BOOL isOk;

/**
 *  Error description
 */
@property (readonly, getter=getMessage) NSString* message;

+ (instancetype) errorWithCode:(DroiErrorCode) code ticket:(NSString*) ticket;
+ (instancetype) errorWithCode:(DroiErrorCode) code ticket:(NSString*) ticket appendedMessage:(NSString*) message;
@end
