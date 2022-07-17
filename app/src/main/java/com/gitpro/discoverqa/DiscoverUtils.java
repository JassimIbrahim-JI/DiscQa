package com.gitpro.discoverqa;

import java.io.IOException;


public class DiscoverUtils extends IOException {


    @Override
    public String getMessage() {
        return "No Internet Connection";
        // You can send any message whatever you want from here.
    }

//    public static boolean isTablet(Context context) {
//        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
//    }
//
//    public static void saveLocale(String lang, AppCompatActivity context) {
//        String langPref = "Language";
//        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs",
//                AppCompatActivity.MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(langPref, lang);
//        editor.commit();
//    }
//
//
//    public static void loadLocale(AppCompatActivity context) {
//
//        String language = getLocale(context);
//        Locale myLocale = new Locale(language);
//        Locale.setDefault(myLocale);
//        Configuration config = new Configuration();
//        config.setLocale(myLocale);
//        context.getBaseContext()
//                .getResources()
//                .updateConfiguration(
//                        config,
//                        context.getBaseContext().getResources()
//                                .getDisplayMetrics());
//    }
//
//    public static String getLocale(AppCompatActivity context) {
//        String langPref = "Language";
//
//        SharedPreferences prefs = context.getSharedPreferences("CommonPrefs",
//                AppCompatActivity.MODE_PRIVATE);
//        return prefs.getString(langPref, "0");
//    }

}
