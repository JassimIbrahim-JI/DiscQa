package com.gitpro.discoverqa.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.gitpro.discoverqa.networks.ConnectionDetector;
import com.gitpro.discoverqa.R;
import com.gitpro.discoverqa.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class SplashScreenActivity extends AppCompatActivity{

    private static final String TAG = "SplashScreenActivity";
    CoordinatorLayout coordinatorLayout;
    Snackbar snackbar;
    //firebase
    private FirebaseAuth mFirebaseAuth;//g-mail account ,facebook account
    private FirebaseFirestore mFirebaseFirestore;//store data and queries
    private CollectionReference users;
    private FirebaseAuth.AuthStateListener mFirebaseAuthListner;
    private FirebaseUser user;
    public static String lan = "null";
    private boolean internet_checked = false;
    private boolean flag = false;
    ConnectionDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        coordinatorLayout = findViewById(R.id.coordinator);
        checkInternet();
        firebaseLogin();

    }
    private void firebaseLogin() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Log.d(TAG, "onAuthStateChanged:signed_out:");
                    flag=true;


                    List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build(),
                            new AuthUI.IdpConfig.FacebookBuilder().build());

                    AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout.Builder(R.layout.activity_login).
                            setGoogleButtonId(R.id.gmail_btn).setFacebookButtonId(R.id.facbook_btn).build();

                    Intent signInIntent=AuthUI.getInstance().createSignInIntentBuilder().
                            setAvailableProviders(providers).setIsSmartLockEnabled(false,true).
                            setTheme(R.style.AppThemeFirebaseAuth).setAuthMethodPickerLayout(customLayout).build();
                       signInResult.launch(signInIntent);
                }
                else {
                    if (!flag) {
                        setUser();
                    }
                    Log.d(TAG, "onAuthStateChanged:signed_in"+user.getUid());
                }
            }
        };
    }



    private final ActivityResultLauncher<Intent> signInResult=registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result){
        IdpResponse response=result.getIdpResponse();
        if (result.getResultCode()==RESULT_OK){
            Toast.makeText(this, "sign in Successful", Toast.LENGTH_SHORT).show();
            setUser();
        }
        else {
            if (response==null){
                Toast.makeText(this, "sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
            else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                showSnackBar();
                Log.e(TAG, String.valueOf(Objects.requireNonNull(response.getError()).getErrorCode()));
            }
        }

    }

    private void start_activity () {
      //Move to main activity
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void checkInternet() {
       detector=new ConnectionDetector(this);
        if (detector.isConnectToNetwork()) {
            showSnackBar();
        } else {
            internet_checked = true;
        }
    }


//to show if their is no internet connection
 //snackbars are preferred mechanism for displaying feedback messages to users
    private void showSnackBar() {
        snackbar = Snackbar.make(coordinatorLayout, getResources().getString(R.string.lost_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (snackbar.isShown()) {
                            snackbar.dismiss();
                        }
                        checkInternet();
                        onResume();
                    }
                });
        snackbar.show();
    }
    //end of snack bar

    @Override
    protected void onResume() {
        super.onResume();
        if (internet_checked) {
            mFirebaseAuth.addAuthStateListener(mFirebaseAuthListner);
        } else
            showSnackBar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mFirebaseAuthListner);
    }
//end of onAuthStateChanged



//store user in firebase firestore
    protected void setUser() {
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        users = mFirebaseFirestore.collection("users");
        users.whereEqualTo("email", mFirebaseAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> intrests = new ArrayList<>();
                List<String> toursCommentedOn = new ArrayList<>();
               User mUser;
                if (queryDocumentSnapshots.isEmpty()) {
                    DocumentReference newUserRefernce = users.document();
                    mUser = new User(mFirebaseAuth.getCurrentUser().getDisplayName(),
                            mFirebaseAuth.getCurrentUser().getEmail(),
                            mFirebaseAuth.getCurrentUser().getPhotoUrl().toString() + "?height=500",
                            intrests,
                            false,
                            toursCommentedOn,
                            newUserRefernce.getId());

                    newUserRefernce.set(mUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Log.d(TAG, "onComplete: " + " new user registered");
                            else
                                Log.d(TAG, "Failed!!");
                        }
                    });
                } else {
                    User user = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                    if (!user.imageURL.equals(mFirebaseAuth.getCurrentUser().getPhotoUrl().toString()) || !user.displayName.equals(mFirebaseAuth.getCurrentUser().getDisplayName())) {
                        final DocumentReference userRef = mFirebaseFirestore.collection("users").document(user.userId);

                        Map<String, Object> updatedData = new HashMap<>();
                        updatedData.put("imageURL", mFirebaseAuth.getCurrentUser().getPhotoUrl().toString() + "?height=500");
                        updatedData.put("displayName", mFirebaseAuth.getCurrentUser().getDisplayName());

                        userRef.update(updatedData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: user updated successfully");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Failed to update user image");
                            }
                        });

                    }
                }

                start_activity();
            }
        });
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration conf = new Configuration();
        conf.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(conf, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor pref = getSharedPreferences("settings", MODE_PRIVATE).edit();
        pref.putString("my_lang", lang);
        pref.apply();
    }

    public void loadLocale() {
        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
        String lang = pref.getString("my_lang", "en");
        if (lang.equals("ar")) {
            lan = "ar";
        } else {
            lan = "en";
        }
        setLocale(lang);
    }



}
