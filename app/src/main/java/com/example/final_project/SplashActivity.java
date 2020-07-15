package com.example.final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.final_project.CheckService.GoogleServices;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Arrays;

public class SplashActivity extends AppCompatActivity {

    /*This a splash screen which is going to show at the stating*/

    /**
     * @Variable provides ,Is use to store the values of all the authentication which are
     * going to add in this application.
     *
     * @Variable RC_SIGN_IN ,Is use the store the value for Return Statement for Sing in methods
     *
     * @Variable mFirebaseAuth ,Is use to get the state of the app
     *
     * @Variable mAuthStateListener ,Is use to the listen the state of the user sign_in or sign_out
     * **/

    public final static int RC_SIGN_IN = 7;
    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /**@Decleartion provides, Here we are going to make permissions of Email,Phone and Gmail.
         * @Initialize , The mFirebaseAuth.
         * **/
        mFirebaseAuth = FirebaseAuth.getInstance();
        //Anonymous user are only for development purpose on release it get removed
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build());
//                new AuthUI.IdpConfig.FacebookBuilder().build(),
//                new AuthUI.IdpConfig.TwitterBuilder().build());

        /**
         * @Variable actionCodeSetting ,Is use to store the values of the email verification code generated by firebase
         **/
        /*ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName(/* yourPackageName= */// , /* installIfNotAvailable= */ true,
        /* minimumVersion= */ //null)
      /*  .setHandleCodeInApp(true) // This must be set to true
                .setUrl("https://google.com") // This URL needs to be whitelisted
                .build();*/


        //Checking for google play services
        if (!GoogleServices.checkGooglePlayServices(this)) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("No Google Play Services");
            alertDialog.setMessage("This app requires Google Play Services to be installed and enabled");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                            System.exit(0);
                        }
                    });
            alertDialog.show();

        }else{
            mAuthStateListener = firebaseAuth -> {
                //@Variable user, Is store the user currently sign in ,if no user logged in it show null
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //Checking is the user is logged in
                if(user != null){
                    //Toast.makeText(getApplicationContext(),"You are Signed in, Welcome to Project Android",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }else{
                    //Else statement run if the use is not sign in

                    // Create and launch sign-in intent
                    //for lambda function ()-> use java version 1.8+ low java version do't support lambda function
                    new Handler().postDelayed(()->startActivityForResult(AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setLogo(R.mipmap.ic_launcher_round)
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN),1000);
                }
            };
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(getApplicationContext(),"Your are Logged in "+user.getDisplayName(),Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, UserNameImageActivity.class));
                // ...
            } else {
                Toast.makeText(getApplicationContext(),"Sorry you aren't able to make it",Toast.LENGTH_SHORT).show();
                finish();
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        //Removing the Authentication Listener when app get pause
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        //Adding the Authentication Listener when app get Resume
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}