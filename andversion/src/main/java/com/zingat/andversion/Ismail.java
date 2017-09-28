package com.zingat.andversion;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by ismailgungor on 28.09.2017.
 */

public class Ismail implements AndVersionContract.View {

    private Activity activity;

    @Inject
    AndVersionPresenter mPresenter;

    public Ismail( Activity activity ) {
        this.activity = activity;
        DaggerAndVersionComponent.builder()
                .andVersionModule( new AndVersionModule() )
                .build().inject( this );

        mPresenter.setView( this );

    }

    public void checkUpdate( String url ) throws IOException {
        this.mPresenter.getJsonFromUrl( url );

    }

    @Override
    public void makeToast( final String response ) {

        activity.runOnUiThread( new Runnable() {
            @Override
            public void run() {
                Toast.makeText( activity, response, Toast.LENGTH_SHORT ).show();

            }
        } );
    }
}
