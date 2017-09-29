package com.zingat.andversion;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

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
        mPresenter.setPackageInfoForPresenter( this.activity );

    }

    public void checkUpdate( String url ) {
        try {
            this.mPresenter.getJsonFromUrl( url );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

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

    @Override
    public void showForceUpdateDialogs( final String whatsNew ) {

        activity.runOnUiThread( new Runnable() {
            @Override
            public void run() {
                new MaterialDialog.Builder( activity )
                        .cancelable( false )
                        .title( activity.getString( R.string.title_before_update ) )
                        .content( whatsNew )
                        .positiveText( activity.getString( R.string.button_update ) )
                        .negativeText( activity.getString( R.string.button_exit_app ) )
                        .onNegative( new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick( @NonNull MaterialDialog dialog, @NonNull DialogAction which ) {
                                activity.finish();
                            }
                        } )
                        .show();
            }
        } );


    }
}
