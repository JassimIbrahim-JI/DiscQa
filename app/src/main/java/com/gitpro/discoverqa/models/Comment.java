package com.gitpro.discoverqa.models;

import com.google.firebase.firestore.IgnoreExtraProperties;


//model class
// @IgnoreExtraProperties Properties that don't map to class fields are ignored when serializing to a class annotated with this annotation.
@IgnoreExtraProperties
public class Comment {
    public User user;
    public String comment;
    public float rating;
    public String time;

    public Comment(User user, String comment, float rating, String time) {
        this.user = user;
        this.comment = comment;
        this.rating = rating;
        this.time = time;
    }

    public Comment() { }//needed to firebase

}
