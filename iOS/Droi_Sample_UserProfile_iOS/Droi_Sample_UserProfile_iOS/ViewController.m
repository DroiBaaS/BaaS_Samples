//
//  ViewController.m
//  Droi_Sample_UserProfile_iOS
//
//  Created by Jerry Chan on 2017/9/25.
//  Copyright © 2017年 Jerry Chan. All rights reserved.
//

#import "ViewController.h"
#import "LSLDatePickerDialog.h"
#import "MyUser.h"

@interface ViewController () <UINavigationControllerDelegate, UIImagePickerControllerDelegate>
@property (weak, nonatomic) IBOutlet UIButton *btnLogin;
@property (weak, nonatomic) IBOutlet UIImageView *ivPhoto;
@property (weak, nonatomic) IBOutlet UIButton *btnGender;
@property (weak, nonatomic) IBOutlet UIButton *btnBirthday;
@property (weak, nonatomic) IBOutlet UITextField *textName;
@property (weak, nonatomic) IBOutlet UITextField *textAddress;
@property NSArray* nameViews;
@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Do any additional setup after loading the view, typically from a nib.
    self.nameViews = @[ self.textName, self.textAddress, self.btnGender, self.btnBirthday ];
    [self refreshUI];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Click Handler -
- (IBAction)clickLoginButton:(id)sender {
    self.btnLogin.userInteractionEnabled = NO;
    
    [DroiUser loginByUserClassInBackground:@"TestUserIOS" password:@"PASSWORD" userClass:MyUser.class callback:^(DroiUser *user, DroiError *error) {
        if ( error.isOk ) {
            // Login OK
            dispatch_async( dispatch_get_main_queue(), ^{
                [self refreshUI];
            });
        } else if (error.code == DROICODE_USER_NOT_EXISTS ) {
            // SignUp
            MyUser* user = [MyUser new];
            user.name = @"MyUser";
            user.birthday = [NSDate date];
            user.address = @"Address";
            user.gender = NO;
            
            // Default icon to byte array
            UIImage* image = [UIImage imageNamed:@"Camera"];
            NSData* bitmapdata = UIImageJPEGRepresentation(image, 0.8f);
            user.photo = [DroiFile fileWithData:bitmapdata];
            
            // Create a new MyUser
            user.UserId = @"TestUserIOS";
            user.Password = @"PASSWORD";
            
            [user signUpInBackground:^(BOOL result, DroiError *error) {
                if ( error.isOk ) {
                    dispatch_async( dispatch_get_main_queue(), ^{
                        [self refreshUI];
                    });
                } else {
                    self.btnLogin.userInteractionEnabled = YES;
                }
            }];
        } else {
            self.btnLogin.userInteractionEnabled = YES;
        }
    }];
}

- (IBAction)clickPhotoButton:(id)sender {
    UIImagePickerController *imagePicker = [[UIImagePickerController alloc] init];
    
    // Open picker type
    imagePicker.sourceType = UIImagePickerControllerSourceTypeSavedPhotosAlbum;
    imagePicker.mediaTypes =
    [UIImagePickerController availableMediaTypesForSourceType:
     UIImagePickerControllerSourceTypeSavedPhotosAlbum];
    imagePicker.delegate = self;
    //
    // Show photo picker
    [self presentViewController:imagePicker animated:NO completion:nil];
}

- (IBAction)clickGenderButton:(id)sender {
    MyUser* user = [DroiUser getCurrentUserByUserClass:MyUser.class];
    if ( user == nil || user.isAnonymous|| !user.isAuthorized)
        return;

    user.gender ^= YES;
    [self refreshUI];
    [self updateDataInBackground:user];
}

- (IBAction)clickBirthdayButton:(id)sender {
    MyUser* user = [DroiUser getCurrentUserByUserClass:MyUser.class];
    if ( user == nil || user.isAnonymous|| !user.isAuthorized)
        return;
    // Display
    LSLDatePickerDialog* datePicker = [[LSLDatePickerDialog alloc] init];
    [datePicker showWithTitle:@"DatePicker" doneButtonTitle:@"Done" cancelButtonTitle:@"Cancel" defaultDate:user.birthday minimumDate:nil maximumDate:nil datePickerMode:UIDatePickerModeDate callback:^(NSDate * _Nullable date)
    {
           if(date)
           {
               user.birthday = date;
               dispatch_async( dispatch_get_main_queue(), ^{
                   [self updateDataInBackground:user];
                   [self refreshUI];
               });

           }
    }];
}

- (IBAction)inputEndOnExit:(id)sender {
    if ( sender == self.textName ) {
        [self.textAddress becomeFirstResponder];
    } else if ( sender == self.textAddress ) {
        [self.textAddress resignFirstResponder];
    } else {
        [sender resignFirstResponder];
        return;
    }
    
    MyUser* user = [DroiUser getCurrentUserByUserClass:MyUser.class];
    if ( user == nil || user.isAnonymous|| !user.isAuthorized)
        return;

    if ( sender == self.textName )
        user.name = self.textName.text;
    else if ( sender == self.textAddress )
        user.address = self.textAddress.text;
    
    [self updateDataInBackground:user];
}


#pragma mark - UIImagePickerControllerDelegate -
- (void) imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary<NSString *,id> *)info {
    UIImage *img = [info objectForKey:UIImagePickerControllerEditedImage];
    if(!img) img = [info objectForKey:UIImagePickerControllerOriginalImage];

    [picker dismissViewControllerAnimated:NO completion:nil];
    if ( img == nil )
        return;
    
    // Get current user and check whether this is authorized user
    MyUser* user = [DroiUser getCurrentUserByUserClass:MyUser.class];
    if ( user == nil || user.isAnonymous|| !user.isAuthorized)
        return;

    //
    self.ivPhoto.image = img;
    NSData* bitmapdata = UIImageJPEGRepresentation(img, 0.8f);
    if ( user.photo == nil ) {
        // Create new DroiFile
        user.photo = [DroiFile fileWithData:bitmapdata];
        [self updateDataInBackground:user];
    } else {
        [user.photo updateDataInBackground:bitmapdata callback:^(BOOL result, DroiError *error) {
            NSLog( @"Update photo data. Result is %d, %@", error.isOk, error );
            [user saveInBackground:nil];
        }];
    }
}


#pragma mark - Common Methods -
- (void) updateDataInBackground:(MyUser*) user {
    [user saveInBackground:^(BOOL result, DroiError *error) {
        NSLog( @"Updated. Result is %d", error.isOk );
    }];
}

- (void) refreshUI {
    MyUser* user = [DroiUser getCurrentUserByUserClass:MyUser.class];
    NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
    [dateFormat setDateFormat:@"YYYY/MM/dd"];
    
    
    if ( user == nil || user.isAnonymous || !user.isAuthorized ) {
        self.textName.text = @"";
        self.textAddress.text = @"";
        [self.btnBirthday setTitle:[dateFormat stringFromDate:[NSDate date]] forState:UIControlStateNormal];
        [self.btnGender setTitle:@"Male" forState:UIControlStateNormal];

        self.btnLogin.userInteractionEnabled = YES;
        [self.nameViews enumerateObjectsUsingBlock:^(UIView*  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            obj.userInteractionEnabled = NO;
        }];
        return; // This is no naming user login.
    }
    self.btnLogin.userInteractionEnabled = NO;
    [self.nameViews enumerateObjectsUsingBlock:^(UIView*  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        obj.userInteractionEnabled = YES;
    }];
    
    // Set text data
    self.textName.text = user.name;
    [self.btnBirthday setTitle:[dateFormat stringFromDate:user.birthday] forState:UIControlStateNormal];
    [self.btnGender setTitle:(user.gender?@"Male":@"Female") forState:UIControlStateNormal];
    self.textAddress.text = user.address;

    // Set the photo
    if ( user.photo != nil ) {
        [user.photo getInBackground:^(NSData *data, DroiError *error) {
            if ( error.isOk == NO ) {
                NSLog( @"getInBackground failed. error is %@", error );
                return;
            }
            
            // Set the photo
            NSLog( @"getInBackground OK" );
            UIImage* image = [UIImage imageWithData:data];
            dispatch_async( dispatch_get_main_queue(), ^{
                self.ivPhoto.image = image;
            });
        }];
    }
}
@end
