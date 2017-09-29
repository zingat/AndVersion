package com.zingat.andversion;

import android.app.Activity;

import java.io.IOException;

/**
 * Created by ismailgungor on 28.09.2017.
 */

public interface AndVersionContract {

    interface View {

        void makeToast( String response );

        void showForceUpdateDialogs( String whatsNew );

    }

    interface Presenter {

        void setView( View view );

        void getJsonFromUrl( String url ) throws IOException;

        void setPackageInfoForPresenter( Activity activity );


    }
}
