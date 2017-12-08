package com.zingat.andversion;

import android.app.Activity;

import java.io.IOException;
import java.util.HashMap;


public interface AndVersionContract {

    interface View {

        void showForceUpdateDialogs( String whatsNew, String packageName );

        void checkLastSessionVersion( String features, int currentUdateVersion );

        void checkNewsLastSessionVersion( String features, int currentUdateVersion );

        void showNews( String features, OnCompletedListener completedListener );

        void showNews( String features );

    }

    interface Presenter {

        void setView( View view );

        void setActivity( Activity activity );

        void getJsonFromUrl( String url, OnCompletedListener onCompletedListener ) throws IOException;

        void setPackageInfoForPresenter();

        void checkLastSessionVersion( String features, int currentUpdateVersion );

        void sendUserToGooglePlay( String packageName );

        void getForceUpdateInfoFromUrl( String url, OnCompletedListener onCompletedListener );

        void getVersionInfoFromUrl( String url );

        void checkNewsLastSessionVersion( String features, int currentUpdateVersion );


    }
}
