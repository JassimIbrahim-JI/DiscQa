package com.gitpro.discoverqa.networks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gitpro.discoverqa.networks.DiscoverUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

// Check Connection To Internet

public class ConnectionDetector implements Interceptor {

    private final Context mContext;

    public ConnectionDetector(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (isConnectToNetwork()){
            throw new DiscoverUtils();
        }
        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }

    @SuppressLint("ObsoleteSdkInt")
    public boolean isConnectToNetwork(){
        boolean checkInternet=false;
        try {

            ConnectivityManager connectivityManager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                return true;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities cap = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (cap == null) {
                    return true;
                }

                return !cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = connectivityManager.getAllNetworks();
                for (Network n: networks) {
                    NetworkInfo nInfo = connectivityManager.getNetworkInfo(n);
                    if (nInfo != null && nInfo.isConnected()) checkInternet=true;
                }
            } else {
                NetworkInfo networks = connectivityManager.getActiveNetworkInfo();
                if (networks.isAvailable() && networks.isConnected())
                    checkInternet=true;
            }

        }
        catch (Exception e){
            Log.e("Error network",e.getMessage());
        }
        return !checkInternet;
    }
}

