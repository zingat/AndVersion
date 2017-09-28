package com.zingat.andversion;

import java.io.IOException;

/**
 * Created by ismailgungor on 28.09.2017.
 */

public interface AndVersionContract {

    interface View {

        void makeToast( String response );

    }

    interface Presenter {

        void setView( View view );

        void getJsonFromUrl( String url ) throws IOException;

    }
}
