package com.zingat.andversion;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zingat.andversion.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

class AndVersionPresenter implements AndVersionContract.Presenter {

    private AndVersionContract.View mView;
    private OkHttpClient mClient;
    private JsonParseHelper mJsonParseHelper;
    private int currentVersionCode;
    private int lastSessionVersion;
    private String packageName;
    private Activity activity;
    private OnCompletedListener mCompletedListener;

    private int minSupportVersion;
    private int currentUpdateVersion;

    private Bundle extras = new Bundle();

    private Handler handler = new Handler( Looper.getMainLooper() ) {

        @Override
        public void handleMessage( Message msg ) {
            super.handleMessage( msg );

        }
    };

    AndVersionPresenter() {
        this.mClient = new OkHttpClient();
        this.mJsonParseHelper = new JsonParseHelper();
    }

    @Override
    public void setView( AndVersionContract.View view ) {
        this.mView = view;

    }

    @Override
    public void setActivity( Activity activity ) {
        this.activity = activity;
    }

    @Override
    public void setPackageInfoForPresenter() {

        try {
            PackageInfo packageInfo = this.activity.getPackageManager().getPackageInfo( activity.getPackageName(), 0 );
            this.currentVersionCode = packageInfo.versionCode;
            this.packageName = packageInfo.packageName;

        } catch ( PackageManager.NameNotFoundException e ) {
            e.printStackTrace();
        }

    }

    @Override
    public void checkLastSessionVersion( Activity activity, String features, int currentUpdateVersion ) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( activity );
        this.lastSessionVersion = preferences.getInt( Constants.LAST_SESSION_VERSION, 0 );

        SharedPreferences.Editor editor = preferences.edit();

        if ( this.lastSessionVersion != this.currentVersionCode ) {

            if ( this.lastSessionVersion != 0 && this.currentVersionCode == currentUpdateVersion ) {

                mView.showUpdateFeatures( features, this.mCompletedListener );


            } else {
                this.mCompletedListener.onCompleted();
            }

            this.lastSessionVersion = this.currentVersionCode;
            editor.putInt( Constants.LAST_SESSION_VERSION, this.lastSessionVersion );
            editor.apply();


        } else {
            this.mCompletedListener.onCompleted();
        }

    }

    @Override
    public void sendUserToGooglePlay( String packageName ) {
        try {
            this.activity.startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=" + packageName ) ) );
        } catch ( android.content.ActivityNotFoundException anfe ) {
            this.activity.startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( "https://play.google.com/store/apps/details?id=" + packageName ) ) );
        }
    }

    @Override
    public void getJsonFromUrl( @NonNull String url, @Nullable final OnCompletedListener completedListener ) throws IOException {

        this.mCompletedListener = completedListener;
        Request request = new Request.Builder()
                .url( url )
                .build();


        mClient.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure( @NonNull Call call, @NonNull IOException e ) {
                if ( completedListener != null )
                    completedListener.onCompleted();
            }

            @Override
            public void onResponse( @NonNull Call call, @NonNull Response response ) throws IOException {

                ResponseBody responseBody = response.body();

                if ( responseBody != null ) {
                    try {

                        mJsonParseHelper.setAndVersionObject( new JSONObject( responseBody.string() ) );
                        minSupportVersion = mJsonParseHelper.getMinSupportVersion();
                        currentUpdateVersion = mJsonParseHelper.getCurrentVersion();

                        ArrayList< String > whatsNew = mJsonParseHelper.getWhatsNew();

                        String features = "";
                        if ( whatsNew != null ) {
                            for ( int i = 0; i < whatsNew.size(); i++ ) {
                                features = features + "- " + whatsNew.get( i ) + "\n";
                            }
                        }

                        if ( currentUpdateVersion != -1 && minSupportVersion != -1 ) {

                            if ( currentVersionCode < minSupportVersion ) {
                                mView.showForceUpdateDialogs( features, packageName );
                                return;

                            } else {

                                if ( !features.equals( "" ) ) {
                                    mView.checkLastSessionVersion( features, currentUpdateVersion );

                                    return;
                                }

                            }
                        }

                    } catch ( JSONException e ) {
                        e.printStackTrace();

                    }
                }
                if ( completedListener != null )
                    completedListener.onCompleted();
            }
        } );

    }

    @Override
    public HashMap< String, String > getStringValues() {

        HashMap< String, String > stringValuesMap = new HashMap<>();

        String[] stringKeys = this.activity.getResources().getStringArray( R.array.string_keys );
        String[] stringValues = this.activity.getResources().getStringArray( R.array.string_values );

        for ( int i = 0; i < stringKeys.length; i++ ) {

            int identifier = this.activity.getResources().getIdentifier( stringKeys[i], "string", packageName );
            String stringValue;

            if ( identifier != 0 ) {

                stringValue = this.activity.getResources().getString( identifier );
                stringValuesMap.put( stringKeys[i], stringValue );
            } else {

                stringValue = stringValues[i];
                stringValuesMap.put( stringKeys[i], stringValue );
            }

        }
        return stringValuesMap;
    }

    @Override
    public void getForceUpdateInfoFromUrl( String url, final OnCompletedListener onCompletedListener ) {

        this.mCompletedListener = onCompletedListener;
        Request request = new Request.Builder()
                .url( url )
                .build();

        mClient.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure( @NonNull Call call, @NonNull IOException e ) {
                mCompletedListener.onCompleted();
            }

            @Override
            public void onResponse( @NonNull Call call, @NonNull Response response ) throws IOException {
                ResponseBody responseBody = response.body();
                if ( responseBody != null ) {

                    try {

                        mJsonParseHelper.setAndVersionObject( new JSONObject( responseBody.string() ) );
                        int minSupportVersion = mJsonParseHelper.getMinSupportVersion();
                        ArrayList< String > whatsNew = mJsonParseHelper.getWhatsNew();

                        String features = "";
                        if ( whatsNew != null ) {
                            for ( int i = 0; i < whatsNew.size(); i++ ) {
                                features = features + "- " + whatsNew.get( i ) + "\n";
                            }
                        }

                        if ( minSupportVersion != -1 ) {

                            if ( currentVersionCode < minSupportVersion ) {
                                mView.showForceUpdateDialogs( features, packageName );
                                return;

                            }

                        }


                    } catch ( JSONException e ) {
                        e.printStackTrace();
                    }

                }
                mCompletedListener.onCompleted();

            }
        } );
    }

    @Override
    public void getVersionInfoFromUrl( String url ) {

        Request request = new Request.Builder()
                .url( url )
                .build();

        mClient.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure( @NonNull Call call, @NonNull IOException e ) {
                e.printStackTrace();
            }

            @Override
            public void onResponse( @NonNull Call call, @NonNull Response response ) throws IOException {
                ResponseBody responseBody = response.body();

                if ( responseBody != null ) {
                    try {
                        String features = parseFeaturesContent( responseBody );
                        if ( currentUpdateVersion != -1 && !features.equals( "" ) ) {
                            mView.checkNewsLastSessionVersion( features, currentUpdateVersion );
                        }

                    } catch ( JSONException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } );

    }

    @Override
    public void checkNewsLastSessionVersion( final String features, int currentUpdateVersion ) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( this.activity );
        this.lastSessionVersion = preferences.getInt( Constants.LAST_SESSION_VERSION, 0 );

        SharedPreferences.Editor editor = preferences.edit();

        if ( this.lastSessionVersion != this.currentVersionCode ) {

            if ( this.lastSessionVersion != 0 && this.currentVersionCode <= currentUpdateVersion && this.currentVersionCode == currentUpdateVersion ) {

                this.activity.runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        mView.showNews( features );
                    }
                } );
            }

            this.lastSessionVersion = this.currentVersionCode;
            editor.putInt( Constants.LAST_SESSION_VERSION, this.lastSessionVersion );
            editor.apply();

        }
    }

    private String parseFeaturesContent( ResponseBody responseBody ) throws IOException, JSONException {

        mJsonParseHelper.setAndVersionObject( new JSONObject( responseBody.string() ) );
        minSupportVersion = mJsonParseHelper.getMinSupportVersion();
        currentUpdateVersion = mJsonParseHelper.getCurrentVersion();
        ArrayList< String > whatsNew = mJsonParseHelper.getWhatsNew();

        StringBuilder features = new StringBuilder();
        if ( whatsNew != null ) {
            for ( int i = 0; i < whatsNew.size(); i++ ) {
                features.append( "- " ).append( whatsNew.get( i ) ).append( "\n" );
            }
        }

        return features.toString();
    }

}
