package com.zingat.andversion;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.zingat.andversion.constants.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

class AndVersionPresenter implements AndVersionContract.Presenter {

    private AndVersionContract.View mView;
    private String packageName;
    private Activity activity;
    private OnCompletedListener mCompletedListener;
    private ParsedContentModel parsedContentModel;
    private String uri;
    private HashMap< String, String > header = new HashMap<>();

    /**
     * Indicates application's current version code
     * that is defined in Gradle file.
     */
    private int currentVersionCode;

    /**
     * Indicates the last saved last version code
     * to {@link SharedPreferences}
     */
    private int lastSessionVersion;


    AndVersionPresenter() {
        this.parsedContentModel = new ParsedContentModel();
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
    public void sendUserToGooglePlay( String packageName ) {
        try {
            this.activity.startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=" + packageName ) ) );
        } catch ( android.content.ActivityNotFoundException anfe ) {
            this.activity.startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( "https://play.google.com/store/apps/details?id=" + packageName ) ) );
        }
    }

    @Override
    public void checkUpdateRules( final ParsedContentModel parsedContentModel ) {

        activity.runOnUiThread( new Runnable() {
            @Override
            public void run() {
                if ( parsedContentModel.getCurrentUpdateVersion() != -1 && parsedContentModel.getMinSupportVersion() != -1 ) {
                    if ( currentVersionCode < parsedContentModel.getMinSupportVersion() ) {
                        mView.showForceUpdateDialog( parsedContentModel.getFeatures(), packageName );
                        return;
                    }
                }

                if ( mCompletedListener != null )
                    mCompletedListener.onCompleted();
            }
        } );

    }

    @Override
    public void checkNewVersionFeatures( final ParsedContentModel parsedContentModel ) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( activity );
        this.lastSessionVersion = preferences.getInt( Constants.LAST_SESSION_VERSION, 0 );

        SharedPreferences.Editor editor = preferences.edit();

        if ( this.lastSessionVersion != this.currentVersionCode ) {
            if ( this.lastSessionVersion != 0 && this.currentVersionCode == parsedContentModel.getCurrentUpdateVersion() ) {
                mView.showNews( parsedContentModel.getFeatures(), this.mCompletedListener );

            } else {
                if ( this.mCompletedListener != null )
                    this.mCompletedListener.onCompleted();
            }

            this.lastSessionVersion = this.currentVersionCode;
            editor.putInt( Constants.LAST_SESSION_VERSION, this.lastSessionVersion );
            editor.apply();

        } else {
            if ( this.mCompletedListener != null )
                this.mCompletedListener.onCompleted();
        }

    }

    @Override
    public void checkForceUpdate( final ParsedContentModel parsedContentModel ) {
        activity.runOnUiThread( new Runnable() {
            @Override
            public void run() {
                if ( parsedContentModel.getMinSupportVersion() != -1 ) {
                    if ( currentVersionCode < parsedContentModel.getMinSupportVersion() ) {
                        mView.showForceUpdateDialog( parsedContentModel.getFeatures(), packageName );
                        return;
                    }
                }

                if ( mCompletedListener != null )
                    mCompletedListener.onCompleted();
            }
        } );
    }

    @Override
    public void setUri( String uri ) {
        this.uri = uri;
    }

    @Override
    public void addHeader( String key, String value ) {
        this.header.put( key, value );
    }

    @Override
    public void getJsonFromUrl(
            @Nullable final OnCompletedListener completedListener,
            @NonNull final IServerResponseListener serverResponseListener ) throws IOException {
        try {

            this.mCompletedListener = completedListener;

            // Create OkHttp Client
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if ( BuildConfig.DEBUG ) {
                builder.addNetworkInterceptor( new StethoInterceptor() );
            }
            OkHttpClient client = builder.build();

            // Create Request instance
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.addHeader( "Accept", "application/json" );

            if( this.header != null ){
                for (Map.Entry<String, String> entry : this.header.entrySet()) {
                    requestBuilder.addHeader( entry.getKey(), entry.getValue() );
                }
            }
            Request request = requestBuilder
                    .url( this.uri )
                    .build();

            client.newCall( request ).enqueue( new Callback() {
                @Override
                public void onFailure( @NonNull Call call, @NonNull IOException e ) {
                    activity.runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            if ( completedListener != null )
                                completedListener.onCompleted();
                        }
                    } );
                }

                @Override
                public void onResponse( @NonNull Call call, @NonNull Response response ) throws IOException {

                    ResponseBody responseBody = response.body();

                    if ( responseBody != null && response.body() != null ) {
                        try {
                            serverResponseListener.onParsedData( parseFeaturesContent( responseBody ) );
                            return;

                        } catch ( JSONException e ) {
                            e.printStackTrace();

                        }
                    }
                    activity.runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            if ( completedListener != null )
                                completedListener.onCompleted();
                        }
                    } );

                }
            } );

        } catch ( IllegalArgumentException ex ) {
            ex.printStackTrace();
            activity.runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    if ( completedListener != null )
                        completedListener.onCompleted();
                }
            } );
        }

    }

    private ParsedContentModel parseFeaturesContent( ResponseBody responseBody ) throws IOException, JSONException {

        JsonParseHelper jsonParseHelper = new JsonParseHelper();
        jsonParseHelper.setAndVersionObject( new JSONObject( responseBody.string() ) );

        ParsedContentModel parsedContentModel = new ParsedContentModel();
        parsedContentModel.setMinSupportVersion( jsonParseHelper.getMinSupportVersion() );
        parsedContentModel.setCurrentUpdateVersion( jsonParseHelper.getCurrentVersion() );

        ArrayList< String > whatsNew = jsonParseHelper.getWhatsNew();
        StringBuilder features = new StringBuilder();
        if ( whatsNew != null ) {
            for ( int i = 0; i < whatsNew.size(); i++ ) {
                features.append( "- " ).append( whatsNew.get( i ) ).append( "\n" );
            }
        }

        parsedContentModel.setFeatures( features.toString() );

        return parsedContentModel;
    }

}
