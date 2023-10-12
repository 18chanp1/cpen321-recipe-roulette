package com.beaker.recipeRoulette;



import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;


import android.app.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.Calendar;

import io.reactivex.rxjava3.core.Single;


public class MainActivity extends AppCompatActivity {
    //Elements
    private SignInButton signInButton;

    // Handle Google Sign In
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private BeginSignInRequest signUpRequest;
    private static final String TAG = "MainActivity";
    private Calendar timeUntilNextSignIn;
    private static final int SIGNIN_TIMEOUT = 10;
    private RxDataStore<Preferences> dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "creating");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.sign_in_button);
        setupSignIn();

        //setup login state
        dataStore = new RxPreferenceDataStoreBuilder(MainActivity.this,  "settings").build();




    }

    private void setupSignIn()
    {
        timeUntilNextSignIn = Calendar.getInstance();

        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
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
                            // String username = credential.getId();
                            String password = credential.getPassword();
                            if (idToken !=  null) {
                                // Got an ID token from Google. Use it to authenticate
                                // with your backend.
                                Log.d(TAG, "Got ID token.");


                                //Writing token and credentials to settings file
                                Single<Preferences> updateResult = dataStore.updateDataAsync(prefsIn ->
                                {
                                    MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();

                                    Preferences.Key<String> tokKey = PreferencesKeys.stringKey("TOKEN");

                                    mutablePreferences.set(tokKey, idToken);
                                    return Single.just(mutablePreferences);
                                });


                            } else if (password != null) {
                                // Got a saved username and password. Use them to authenticate
                                // with your backend.
                                Log.d(TAG, "Got password.");
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



        signInButton.setOnClickListener(view -> oneTapClient.beginSignIn(signUpRequest)
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
                .addOnFailureListener(MainActivity.this, er -> {
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    er.printStackTrace();
                    Toast toast = Toast.makeText(MainActivity.this,"You must be logged into a google account on android first", Toast.LENGTH_LONG);
                    toast.show();
                }));


    }





}