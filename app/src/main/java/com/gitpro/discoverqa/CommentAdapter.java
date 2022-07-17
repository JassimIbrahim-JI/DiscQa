package com.gitpro.discoverqa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

//comment adapter
public class CommentAdapter extends ArrayAdapter<Comment> {
    int tourPosition;

   //constructor
    public CommentAdapter(Context context, List<Comment> comments, int tourPosition) {
        super(context, 0, comments);
        this.tourPosition = tourPosition;
    }
   //Overloading method
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.comment_item, parent, false);
        }
       Comment comment = getItem(position);

        CircleImageView profilePic = listItem.findViewById(R.id.comment_profile_pic);
//picasso library
        Picasso.get().load(comment.user.imageURL).fit().into(profilePic);

        TextView personName = listItem.findViewById(R.id.comment_person_name);
        personName.setText(comment.user.displayName);

        TextView commentText = listItem.findViewById(R.id.comment_main_text);
        commentText.setText(comment.comment);

        RatingBar ratingBar = listItem.findViewById(R.id.comment_rating_bar);
        ratingBar.setRating(comment.rating);

        final TextView noOfReviews = listItem.findViewById(R.id.number_of_reviews);

        //store comment in user document id (in firebase--> cloud firestore)

        FirebaseFirestore mFirebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference users = mFirebaseFirestore.collection("users");
        users.whereEqualTo("email", comment.user.email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                User mUser = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                noOfReviews.setText(mUser.toursCommentedOn.size() + " Reviews");
            }
        });

        TextView time = listItem.findViewById(R.id.comment_time);
        time.setText(comment.time);

        return listItem;
    }
}

