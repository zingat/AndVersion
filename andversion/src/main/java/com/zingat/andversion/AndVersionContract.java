package com.zingat.andversion;

import android.app.Activity;

import java.io.IOException;
import java.util.HashMap;


public interface AndVersionContract {

    interface View {

        void showForceUpdateDialog( String whatsNew, String packageName );

        void showNews( String features, OnCompletedListener completedListener );

        void showNews( String features );

    }

    interface Presenter {

        void setView( View view );

        void setActivity( Activity activity );

        void setPackageInfoForPresenter();

        void getJsonFromUrl( OnCompletedListener onCompletedListener, IServerResponseListener serverResponseListener ) throws IOException;

        void sendUserToGooglePlay( String packageName );

        void checkUpdateRules( ParsedContentModel parsedContentModel );

        void checkNewVersionFeatures( ParsedContentModel parsedContentModel );

        void checkForceUpdate( ParsedContentModel parsedContentModel );

        void setUri( String uri );

        void addHeader( String key, String value );
    }
}
