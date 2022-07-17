package com.gitpro.discoverqa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import javax.annotation.Nullable;


public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {
    public CustomTextView(Context context) {
        super(context);
        init_text(context, null);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init_text(context, attrs);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init_text(context, attrs);
    }

    public void init_text(Context context, AttributeSet attributeSet) {
        if (attributeSet!=null) {
            @SuppressLint({"Recycle", "CustomViewStyleable"}) TypedArray a= context.obtainStyledAttributes(attributeSet,R.styleable.easy_flip_view);
            String fontName=a.getString(R.styleable.easy_flip_view_flipOnTouch);
            try {
                if (fontName!=null){
                    Typeface typeface=Typeface.createFromAsset(context.getAssets(),"font/"+fontName);
                    setTypeface(typeface);
                }
            }
            catch (Exception e){
                 e.printStackTrace();
            }
            a.recycle();
        }

    }


}
