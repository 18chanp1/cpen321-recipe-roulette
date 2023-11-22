package com.beaker.reciperoulette;


import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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




public class MainActivity extends AppCompatActivity {
    //Elements
    private SignInButton signInButton;

    // Handle Google Sign In
    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    private static final String TAG = "MainActivity";
    private Calendar timeUntilNextSignIn;
    private static final int SIGNIN_TIMEOUT = 10;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                    Log.d(TAG, "Can post FCM notifications");
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("No notifications allowed")
                            .setMessage("Since you did not allow notifications, you will not get " +
                                    "information on ingredient sharing. ")
                            .setCancelable(true)
                            .create()
                            .show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "creating");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.sign_in_button);
        setupSignIn();

        //setup login state
        askNotificationPermission();
        MyFirebaseMessagingService.saveFCMTokentoSharedPref(this);

    }

    private void setupSignIn()
    {
        timeUntilNextSignIn = Calendar.getInstance();

        oneTapClient = Identity.getSignInClient(this);

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
                            if (idToken !=  null) {
                                // Got an ID token from Google. Use it to authenticate
                                // with your backend.
                                Log.d(TAG, "Got ID token.");

                                //Writing token and email to settings file
                                SharedPreferences sharedPref =
                                        this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("TOKEN", idToken);
                                editor.putString("EMAIL", username);
                                editor.apply();

                                //Go to main menu
                                try {
                                    String from = getIntent().getExtras().getString("REFRESHSIGNIN");

                                    if(from.equals("RECIPEFACEBOOK"))
                                    {
                                        finish();
                                    }
                                } catch (NullPointerException e)
                                {
                                    Log.d(TAG, "nullpointeered");
                                    Intent mainMenuIntent = new Intent(MainActivity.this, MainMenu.class);
                                    startActivity(mainMenuIntent);
                                }
                            }
                            else {
                                //Writing token and credentials to settings file
                                SharedPreferences sharedPref =
                                        this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("TOKEN", "FAILED");
                                editor.putString("EMAIL", "FAILED");
                                editor.apply();

                                // do nothing
                            }
                        } catch (ApiException e) {
                            Log.d(TAG, e.getMessage());
                        }

                    }
                    else if (result.getResultCode() == Activity.RESULT_CANCELED)
                    {
                        Log.d(TAG, "One tap dialog was closed");
                        timeUntilNextSignIn = Calendar.getInstance();
                        timeUntilNextSignIn.add(Calendar.MINUTE, SIGNIN_TIMEOUT);
                    }
                });


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
                        Toast toast = Toast.makeText(MainActivity.this,"You declined to sign in. You have to wait " + timeDiff + " minutes", Toast.LENGTH_LONG);
                        toast.show();
                    }

                })
                .addOnFailureListener(MainActivity.this, e -> {
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.

                    if(timeUntilNextSignIn.before(Calendar.getInstance())) {
                        Log.d(TAG, e.getLocalizedMessage());
                        Log.d(TAG, "registering new user");

                        oneTapClient.beginSignIn(signUpRequest).addOnSuccessListener(MainActivity.this, result ->
                        {
                            IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                            activityResultLauncher.launch(intentSenderRequest);
                        }).addOnFailureListener(MainActivity.this, er ->
                        {
                            Log.d(TAG, er.getMessage());
                            er.getCause();
                            Toast toast = Toast.makeText(MainActivity.this,"You must be logged into a google account on android first", Toast.LENGTH_LONG);
                            toast.show();
                        });
                    }
                    else {
                        Log.d(TAG, "not showing one tap UI");
                        String timeDiff = String.valueOf((timeUntilNextSignIn.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / (1000 * 60));
                        Toast toast = Toast.makeText(MainActivity.this,"You declined to sign in. You have to wait " + timeDiff + " minutes", Toast.LENGTH_LONG);
                        toast.show();
                    }});


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ActivityResultLauncher<Intent> arl = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());

                        String tok = task.getResult().getIdToken();
                        String email = task.getResult().getEmail();

                        if(tok != null)
                        {
                            SharedPreferences sharedPref =
                                    MainActivity.this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("TOKEN", tok);
                            editor.putString("EMAIL", email);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS))

                new AlertDialog.Builder(this)
                        .setTitle("Allow notification permissions")
                        .setMessage("You must allow the permissions in order to get real time notifications " +
                                "on ingredient sharing")
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            requestPermissionLauncher.launch(POST_NOTIFICATIONS);
                        })
                        .setNegativeButton("Reject notifications", ((dialogInterface, i) -> {
                            //Do nothing
                        }))
                        .create()
                        .show();
        } else {
            // Directly ask for the permission
            requestPermissionLauncher.launch(POST_NOTIFICATIONS);
        }
    }
}