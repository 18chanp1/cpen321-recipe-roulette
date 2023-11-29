package com.beaker.reciperoulette;


import static android.Manifest.permission.POST_NOTIFICATIONS;
import static com.beaker.Utilities.SIGNIN_TIMEOUT;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.beaker.Utilities;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    //Elements
    private SignInButton signInButton;

    // Handle Google Sign In
    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    private static final String TAG = "MainActivity";
    private Calendar timeUntilNextSignIn;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                    Log.d(TAG, "Can post FCM notifications");
                } else {

                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.main_msg_no_perm_title))
                            .setMessage(getString(R.string.main_msg_no_perm_body))
                            .setCancelable(true)
                            .create()
                            .show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.sign_in_button);
        assert signInButton != null;

        boolean signOutNow = getIntent().getBooleanExtra(getString(R.string.menu_signout), false);

        //check if signed in once
        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        long signInTime = sharedPref.getLong(getString(R.string.prf_signin_time), -1);
        boolean isSignedIn = signInTime != -1 &&
                ((Calendar.getInstance().getTimeInMillis() - signInTime) < Utilities.SIGNIN_EXPIRY);


        if(signOutNow) setupSignInButton();
        else if (isSignedIn)
        {
            Intent mainMenuIntent = new Intent(MainActivity.this, MainMenu.class);
            startActivity(mainMenuIntent);
        } else
        {
            setupSignIn();
            setupSignInButton();

            //setup login state
            askNotificationPermission();
            MyFirebaseMessagingService.saveFCMTokentoSharedPref(this);
        }



    }

    private void setupSignIn()
    {
        timeUntilNextSignIn = Calendar.getInstance();
        oneTapClient = Identity.getSignInClient(this);

        assert timeUntilNextSignIn != null;

        //setup sign in request
        BeginSignInRequest signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(false)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client IDs, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

        //setup sign up request for new users
        signUpRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(false)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client IDs, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();


        //setup callback after sign in.
        ActivityResultLauncher<IntentSenderRequest> activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                    if(result.getResultCode() == Activity.RESULT_OK)
                    {
                        try {
                            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                            String idToken = credential.getGoogleIdToken();
                            String username = credential.getId();
                            //For future reference, if we ever need to get the password.
                            // String password = credential.getPassword();
                            if (idToken != null) {
                                // Got an ID token from Google. Use it to authenticate
                                // with your backend.
                                Log.d(TAG, "Got ID token.");

                                //Writing token and email to settings file
                                SharedPreferences sharedPref =
                                        this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(getString(R.string.prf_token), idToken);
                                editor.putString(getString(R.string.prf_eml), username);
                                editor.putLong(getString(R.string.prf_signin_time), Calendar.getInstance().getTimeInMillis());
                                editor.apply();

                                Intent mainMenuIntent = new Intent(MainActivity.this, MainMenu.class);
                                startActivity(mainMenuIntent);
                            }
                            if(idToken == null)
                            {
                                Log.d(TAG, "Id token was null");
                            }
                        } catch (ApiException e) {
                            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                        }

                    }
                    else if (result.getResultCode() == Activity.RESULT_CANCELED)
                    {
                        Log.d(TAG, "One tap dialog was closed");
                        timeUntilNextSignIn = Calendar.getInstance();
                        timeUntilNextSignIn.add(Calendar.MINUTE, SIGNIN_TIMEOUT);
                    }
                });

        //start sign in now.
        //sign in automatically, otherwise, request register
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(MainActivity.this, result -> {
                    if(!timeUntilNextSignIn.after(Calendar.getInstance())) {
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                        activityResultLauncher.launch(intentSenderRequest);
                    }
                    else {
                        Log.d(TAG, "not showing one tap UI");
                        String timeDiff = String.valueOf((timeUntilNextSignIn.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / (1000 * 60));
                        Toast toast = Toast.makeText(MainActivity.this,getString(R.string.main_msg_decl_signin) + timeDiff + getString(R.string.main_msg_decl_signin2), Toast.LENGTH_LONG);
                        toast.show();
                    }

                })
                .addOnFailureListener(MainActivity.this, e -> {
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.

                    if(timeUntilNextSignIn.before(Calendar.getInstance())) {
                        Log.d(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
                        Log.d(TAG, "registering new user");

                        oneTapClient.beginSignIn(signUpRequest).addOnSuccessListener(MainActivity.this, result ->
                        {
                            IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                            activityResultLauncher.launch(intentSenderRequest);
                        }).addOnFailureListener(MainActivity.this, er ->
                        {
                            Log.d(TAG, Objects.requireNonNull(er.getMessage()));
                            er.getCause();
                            Toast toast = Toast.makeText(MainActivity.this,getString(R.string.main_msg_signin_android), Toast.LENGTH_LONG);
                            toast.show();
                        });
                    }
                    else {
                        Log.d(TAG, "not showing one tap UI");
                        String timeDiff = String.valueOf((timeUntilNextSignIn.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / (1000 * 60));
                        Toast toast = Toast.makeText(MainActivity.this, getString(R.string.main_msg_decl_signin) + timeDiff + getString(R.string.main_msg_decl_signin2), Toast.LENGTH_LONG);
                        toast.show();
                    }});
    }

    private void setupSignInButton()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInClient.signOut();

        ActivityResultLauncher<Intent> arl = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK)
                    {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());

                        String tok = task.getResult().getIdToken();
                        String email = task.getResult().getEmail();

                        if(tok != null)
                        {
                            SharedPreferences sharedPref =
                                    MainActivity.this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(getString(R.string.prf_token), tok);
                            editor.putString(getString(R.string.prf_token), email);
                            editor.putLong(getString(R.string.prf_signin_time), Calendar.getInstance().getTimeInMillis());

                            editor.apply();
                        }

                        Intent mainMenuIntent = new Intent(MainActivity.this, MainMenu.class);
                        startActivity(mainMenuIntent);
                    }

                });

        signInButton.setOnClickListener(view ->
        {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            arl.launch(signInIntent);
        });

    }

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)){
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.main_msg_allow_perm))
                        .setMessage(getString(R.string.main_msg_allow_perm_det))
                        .setPositiveButton(getString(R.string.main_msg_perm_OK), (dialogInterface, i) -> requestPermissionLauncher.launch(POST_NOTIFICATIONS))
                        .setNegativeButton(getString(R.string.main_msg_perm_REJ), ((dialogInterface, i) -> {
                            //Do nothing
                        }))
                        .create()
                        .show();
        }
    }
}