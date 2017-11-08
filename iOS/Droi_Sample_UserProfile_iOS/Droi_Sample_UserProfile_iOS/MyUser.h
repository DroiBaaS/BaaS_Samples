
#import <DroiCoreSDK/DroiCoreSDK.h>

@interface MyUser : DroiUser

DroiExpose
@property NSString *name;

DroiExpose
@property NSString *address;
DroiExpose
@property NSDate *birthday;
DroiExpose
@property BOOL gender;  // YES - Male, NO - Female
DroiExpose
@property DroiFile* photo;

@end
