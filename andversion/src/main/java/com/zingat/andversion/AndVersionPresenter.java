package com.zingat.andversion;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.zingat.andversion.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AndVersionPresenter implements AndVersionContract.Presenter {

    private AndVersionContract.View mView;
    private OkHttpClient mClient;
    private JsonParseHelper mJsonParseHelper;
    private int currentVersionCode;
    private int lastSessionVersion;
    private String packageName;
    private Activity activity;
    private OnCompletedListener completedListener;

    @Inject
    AndVersionPresenter() {
        // Silent is golden.
    }

    @Inject
    void setClient( OkHttpClient client ) {
        this.mClient = client;
    }

    @Inject
    void setJsonParseHelper( JsonParseHelper jsonParseHelper ) {
        this.mJsonParseHelper = jsonParseHelper;
    }


    @Override
    public void setView( AndVersionContract.View view ) {

        this.mView = view;

    }


    @Override
    public void setPackageInfoForPresenter( Activity activity ) {

        try {
            this.activity = activity;
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

            if ( this.lastSessionVersion != 0 && this.currentVersionCode <= currentUpdateVersion && this.currentVersionCode == currentUpdateVersion ) {

                mView.showUpdateFeatures( features, this.completedListener );


            } else {
                this.completedListener.onCompleted( "LastSessionVersion NOT Equal currentVersionCode" );
            }

            this.lastSessionVersion = this.currentVersionCode;
            editor.putInt( Constants.LAST_SESSION_VERSION, this.lastSessionVersion );
            editor.apply();


        } else {
            this.completedListener.onCompleted( "LastSessionVersion Equal currentVersionCode" );
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
    public void getJsonFromUrl( String url, final OnCompletedListener completedListener ) throws IOException {

        this.completedListener = completedListener;
        Request request = new Request.Builder()
                .url( url )
                .build();


        mClient.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure( Call call, IOException e ) {
                completedListener.onCompleted( "getJsonFromUrl -> onFailure" );
            }

            @Override
            public void onResponse( Call call, Response response ) throws IOException {

                ResponseBody responseBody = response.body();

                if ( responseBody != null ) {
                    try {

                        mJsonParseHelper.setAndVersionObject( new JSONObject( responseBody.string() ) );
                        int minSupportVersion = mJsonParseHelper.getMinSupportVersion();
                        int currentUpdateVersion = mJsonParseHelper.getCurrentVersion();
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

                            } else {

                                if ( !features.equals( "" ) ) {
                                    mView.checkLastSessionVersion( features, currentUpdateVersion );
                                } else {
                                    completedListener.onCompleted( "Features equals: \"\" " );
                                }

                            }
                        } else {
                            completedListener.onCompleted( "currentUpdate or minSupportVersion error" );
                        }

                    } catch ( JSONException e ) {
                        e.printStackTrace();
                        completedListener.onCompleted( "getJsonFromUrl -> onResponse catch" );
                    }
                }
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

}
