package com.gitpro.discoverqa.calls;

import android.util.Log;

import androidx.annotation.NonNull;

import com.gitpro.discoverqa.models.Place;
import com.gitpro.discoverqa.models.Tour;
import com.gitpro.discoverqa.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirestoreQueries {
//Store Queries
    private static final String TAG = "FirestoreQueries";
   static List<Tour> mTours = new ArrayList<Tour>();
    //Firebase
    private static FirebaseAuth mFirebaseAuth;
    private static FirebaseFirestore mFirebaseFirestore;
//CollectionRef used to adding document, getting document, querying for document
    private static CollectionReference users, places, tours;


    // getting data  user from firebase
    public static void getUser(final FirestoreUsersCallback firestoreCallback){
        //Initialize user permissions
        mFirebaseAuth = FirebaseAuth.getInstance();
         // Initialize cloud FireStore
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        //get document from FireStore user collection
        users = mFirebaseFirestore.collection("users");
        users.whereEqualTo("email",  Objects.requireNonNull(mFirebaseAuth.getCurrentUser()).getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                User mUser = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                firestoreCallback.onCallback(mUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.w(TAG,"Error Fetching config", e);
            }
        });
    }

        public static void getPlaces(final FirestorePlacesCallback firestorePlaceCallback){
       mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        places = mFirebaseFirestore.collection("places");
        //getting places
        places.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<Place> mPlaces = new ArrayList<Place>();

                    for (QueryDocumentSnapshot document :  task.getResult()) {
                        Log.d("Places =",document.getData()+"");
                    mPlaces.add(document.toObject(Place.class));
                    }

                    firestorePlaceCallback.onCallback(mPlaces);
                }else
                    Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    public static void getTours(final FirestoreToursCallback firestoreTourCallback){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        tours = mFirebaseFirestore.collection("tours");
        //getting tours
        tours.orderBy("ratingsNum", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<Tour> mTours = new ArrayList<Tour>();
                    for (QueryDocumentSnapshot document :  task.getResult()) {
                        Log.d("Tours = ",document.getData()+"");
                        mTours.add(document.toObject(Tour.class));
                    }
                    firestoreTourCallback.onCallback(mTours);
                }
                else
                    Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    public interface FirestoreUsersCallback {
        void onCallback(User user);
    }

    public interface FirestorePlacesCallback {
        void onCallback(List<Place> places);
    }

    public interface FirestoreToursCallback {
         void onCallback(List<Tour> tours);
    }


}
