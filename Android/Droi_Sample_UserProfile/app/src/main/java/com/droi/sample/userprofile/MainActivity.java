package com.droi.sample.userprofile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiUser;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView( R.id.photo )
    ImageView imgPhoto;

    @BindView( R.id.editName )
    EditText editName;

    @BindView( R.id.btnGender )
    Button btnGender;

    @BindView( R.id.btnBirthday )
    Button btnBirthday;

    @BindView( R.id.editAddress )
    EditText editAddress;

    @BindView( R.id.login )
    Button btnLogin;

    @BindViews({ R.id.photo, R.id.editName, R.id.btnGender, R.id.btnBirthday, R.id.editAddress })
    List<View> nameViews;

    private final static int PICK_PHOTO = 0x4000;
    private final static String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Binding
        ButterKnife.bind(this);

        final TextView.OnEditorActionListener actionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_NEXT||actionId == EditorInfo.IME_ACTION_DONE) {
                    MyUser user = DroiUser.getCurrentUser( MyUser.class );
                    if ( user == null || user.isAnonymous() || !user.isAuthorized() )
                        return false;

                    // Update data
                    if ( textView == editName ) {
                        user.name = textView.getText().toString();
                        updateDataInBackground(user);
                    } else if ( textView == editAddress ) {
                        user.address = textView.getText().toString();
                        updateDataInBackground(user);
                    }
                }
                return false;
            }
        };

        // Process ENTER event
        editAddress.setOnEditorActionListener( actionListener );
        editName.setOnEditorActionListener( actionListener );

        refreshUI();
    }

    private void refreshUI() {
        MyUser user = DroiUser.getCurrentUser( MyUser.class );
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        editName.setText( "" );
        editAddress.setText( "" );
        btnBirthday.setText( dateFormat.format( new Date() ) );
        btnGender.setText( getString(R.string.male));

        if ( user == null || user.isAnonymous() || !user.isAuthorized() ) {
            btnLogin.setEnabled( true );
            ButterKnife.apply( nameViews, ENABLED, false );
            return; // There is no naming user login.
        }
        btnLogin.setEnabled( false );
        ButterKnife.apply( nameViews, ENABLED, true );

        // Set text data
        editName.setText( user.name );
        btnBirthday.setText( dateFormat.format( user.birthday ) );
        btnGender.setText( user.gender?getString(R.string.male):getString(R.string.female));
        editAddress.setText( user.address );

        // Set the photo
        if ( user.photo != null ) {
            user.photo.getInBackground(
                    new DroiCallback<byte[]>() {
                        @Override
                        public void result(byte[] bytes, DroiError droiError) {
                            if ( droiError.isOk() == false )
                                return;

                            // Set the photo
                            final Drawable image = new BitmapDrawable(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imgPhoto.setImageDrawable( image );
                                }
                            });
                        }
                    }
            );
        }
    }

    private void updateDataInBackground(final MyUser user) {
        user.saveInBackground(new DroiCallback<Boolean>() {
            @Override
            public void result(Boolean aBoolean, DroiError droiError) {
                Log.d(LOG_TAG, "Update. Result is " + droiError.isOk());
            }
        });
    }

    static final ButterKnife.Setter<View, Boolean> ENABLED = new ButterKnife.Setter<View, Boolean>() {
        @Override public void set(View view, Boolean value, int index) {
            view.setEnabled(value);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }

            final MyUser user = DroiUser.getCurrentUser( MyUser.class );
            if ( user == null || user.isAnonymous() || !user.isAuthorized() )
                return;

            try {
                // Update UI
                Uri uri = data.getData();
                Bitmap bm = BitmapFactory.decodeStream( getContentResolver().openInputStream(uri));
                imgPhoto.setImageBitmap( bm );

                // Update data
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byte[] bitmapdata = stream.toByteArray();
                if ( user.photo == null ) {
                    user.photo = new DroiFile(bitmapdata);
                    updateDataInBackground(user);
                } else {
                    user.photo.updateInBackground(bitmapdata, new DroiCallback<Boolean>() {
                        @Override
                        public void result(Boolean aBoolean, DroiError droiError) {
                            Log.d(LOG_TAG, "Update Photo. Result is " + droiError.isOk());

                            // Save
                            user.saveInBackground( null );
                        }
                    });
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    //region Onclick handler
    @OnClick( R.id.login )
    void onClickLogin() {
        btnLogin.setEnabled(false);

        // Login User
        DroiUser.loginInBackground("TestUser", "PASSWORD", MyUser.class, new DroiCallback<DroiUser>() {
            @Override
            public void result(DroiUser droiUser, DroiError droiError) {
                if ( droiError.isOk() ) {
                    // Login OK
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refreshUI();
                        }
                    });

                } else if ( droiError.getCode() == DroiError.USER_NOT_EXISTS ) {
                    // SignUp
                    MyUser user = new MyUser();
                    user.name = "MyUser";
                    user.birthday = new Date();
                    user.address = "Address";
                    user.gender = false;

                    // Default icon to byte array
                    Drawable draw = getResources().getDrawable( R.mipmap.ic_launcher );
                    Bitmap bitmap = ((BitmapDrawable) draw).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    byte[] bitmapdata = stream.toByteArray();
                    user.photo = new DroiFile( bitmapdata );

                    // Create a new MyUser
                    user.setUserId("TestUser");
                    user.setPassword("PASSWORD");
                    user.signUpInBackground(new DroiCallback<Boolean>() {
                        @Override
                        public void result(Boolean aBoolean, DroiError droiError) {
                            if (droiError.isOk()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshUI();
                                    }
                                });
                            } else {
                                btnLogin.setEnabled(true);
                            }

                        }
                    });

                } else {
                    // Display error code
                    btnLogin.setEnabled(true);
                }
            }
        });
    }

    @OnClick( R.id.btnGender )
    void onClickGender() {
        MyUser user = DroiUser.getCurrentUser( MyUser.class );
        if ( user == null || user.isAnonymous() || !user.isAuthorized() )
            return;

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick one");
        List<String> displayValues =  Arrays.asList("Female", "Male");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, displayValues );

        builder.setSingleChoiceItems(adapter, user.gender?1:0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btnGender.setText( (which!=0)?getString(R.string.male):getString(R.string.female));
                dialog.dismiss();

                // Save the update
                MyUser user = DroiUser.getCurrentUser( MyUser.class );
                if ( user == null || user.isAnonymous() || !user.isAuthorized() )
                    return;

                user.gender = (which==0)?false:true;
                updateDataInBackground(user);
            }
        });


        builder.show();
    }

    @OnClick( {R.id.photo, R.id.selectPhoto} )
    void onClickPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO);
    }

    @OnClick( R.id.btnBirthday )
    void onClickBirthday() {
        MyUser user = DroiUser.getCurrentUser( MyUser.class );
        Calendar calendar = Calendar.getInstance();
        if ( user != null && !user.isAnonymous() && user.isAuthorized() ) {
            calendar.setTime( user.birthday );  // Update the date of current user
        }

        // Pick up a day
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                Date date = new Date( year - 1900, month, day );
                btnBirthday.setText( dateFormat.format(date) );

                // Save the update
                MyUser user = DroiUser.getCurrentUser( MyUser.class );
                if ( user == null || user.isAnonymous() || !user.isAuthorized() )
                    return;
                user.birthday = date;

                updateDataInBackground(user);

            }

        }, year,month, day).show();

    }
    //endregion
}
