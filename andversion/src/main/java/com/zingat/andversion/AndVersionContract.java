package com.zingat.andversion;

import android.app.Activity;

import java.io.IOException;
import java.util.HashMap;


public interface AndVersionContract {

    interface View {

        void makeToast( String response );

        void showForceUpdateDialogs( String whatsNew, String packageName );

        void checkLastSessionVersion( String features, int currentUdateVersion );

        void showUpdateFeatures( String features, OnCompletedListener completedListener );

    }

    interface Presenter {

        void setView( View view );

        void getJsonFromUrl( String url, OnCompletedListener onCompletedListener ) throws IOException;

        void setPackageInfoForPresenter( Activity activity );

        void checkLastSessionVersion( Activity activity, String features, int currentUpdateVersion );

        void sendUserToGooglePlay( String packageName );

        HashMap< String, String > getStringValues();

        void getForceUpdateInfoFromUrl( String url, OnCompletedListener onCompletedListener );

    }
}
