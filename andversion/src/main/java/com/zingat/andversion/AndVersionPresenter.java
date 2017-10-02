package com.zingat.andversion;

import android.app.Activity;
import android.content.Context;
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

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by ismailgungor on 28.09.2017.
 */

public class AndVersionPresenter implements AndVersionContract.Presenter {

    private AndVersionContract.View mView;
    private OkHttpClient mClient;
    private JsonParseHelper mJsonParseHelper;
    private String currentVersionName;
    private int currentVersionCode;
    private int lastSessionVersion;
    private String packageName;
    private Activity activity;

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
            this.currentVersionName = packageInfo.versionName;
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

                mView.showUpdateFeatures( features );

            }

            this.lastSessionVersion = this.currentVersionCode;
            editor.putInt( Constants.LAST_SESSION_VERSION, this.lastSessionVersion );
            editor.apply();

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
    public void getJsonFromUrl( String url ) throws IOException {

        Request request = new Request.Builder()
                .url( url )
                .build();


        mClient.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure( Call call, IOException e ) {
                mView.makeToast( "Hata" );
            }

            @Override
            public void onResponse( Call call, Response response ) throws IOException {

                ResponseBody responseBody = response.body();

                if ( responseBody != null ) {
                    try {
                        mJsonParseHelper.setAndVersionObject( new JSONObject( responseBody.string() ) );
                        int minSupportVersion = mJsonParseHelper.getMinSupportVersion();
                        int currentUpdateVersion = mJsonParseHelper.getCurrentVersion();
                        ArrayList< String > trWhatsNew = mJsonParseHelper.getTrWhatsNew();

                        String features = "";
                        for ( int i = 0; i < trWhatsNew.size(); i++ ) {
                            features = features + "- " + trWhatsNew.get( i ) + "\n";
                        }

                        if ( currentUpdateVersion != -1 && minSupportVersion != -1 ) {

                            if ( currentVersionCode < minSupportVersion ) {
                                mView.showForceUpdateDialogs( features, packageName );
                            } else {

                                mView.checkLastSessionVersion( features, currentUpdateVersion );

                            }

                        }


                    } catch ( JSONException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } );


    }

}
