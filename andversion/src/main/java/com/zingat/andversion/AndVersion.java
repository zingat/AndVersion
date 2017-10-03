package com.zingat.andversion;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.zingat.andversion.constants.Constants;

import java.io.IOException;
import java.util.HashMap;

import javax.inject.Inject;

public class AndVersion implements AndVersionContract.View {

    private Activity activity;
    private String uri;
    private static AndVersion andVersion;

    public static AndVersion getInstance( Activity activity ) {
        if ( andVersion == null ) {
            andVersion = new AndVersion( activity );

        }

        return andVersion;
    }

    public void setActivity( Activity activity ) {
        this.activity = activity;
    }

    public void setUri( String uri ) {
        this.uri = uri;
    }

    @Inject
    AndVersionPresenter mPresenter;

    private AndVersion( Activity activity ) {

        DaggerAndVersionComponent.builder()
                .andVersionModule( new AndVersionModule() )
                .build().inject( this );

        this.mPresenter.setView( this );
        this.mPresenter.setPackageInfoForPresenter( activity );

    }

    public void checkUpdate( OnCompletedListener completedListener ) {
        try {
            this.mPresenter.getJsonFromUrl( uri, completedListener );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

    }


    public void checkIsThereForceUpdate( OnCompletedListener completedListener ) {

        this.mPresenter.getForceUpdateInfoFromUrl( uri, completedListener );

    }


    public void checkWhatsNewMessage( OnCompletedListener completedListener ) {

        this.mPresenter.getVersionInfoFromUrl( uri, completedListener );

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
    public void showForceUpdateDialogs( final String whatsNew, final String packageName ) {

        final HashMap< String, String > stringValuesMap = mPresenter.getStringValues();

        activity.runOnUiThread( new Runnable() {
            @Override
            public void run() {

                new MaterialDialog.Builder( activity )
                        .cancelable( false )
                        .title( stringValuesMap.get( Constants.ANDVERSION_FORCEUPDATE_TITLE ) )
                        .content( whatsNew )
                        .positiveText( stringValuesMap.get( Constants.ANDVERSION_UPDATE ) )
                        .negativeText( stringValuesMap.get( Constants.ANDVERSION_EXIT_APP ) )
                        .onNegative( new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick( @NonNull MaterialDialog dialog, @NonNull DialogAction which ) {

                                activity.finish();

                            }
                        } )
                        .onPositive( new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick( @NonNull MaterialDialog dialog, @NonNull DialogAction which ) {

                                mPresenter.sendUserToGooglePlay( packageName );

                            }
                        } )
                        .show();
            }
        } );


    }

    @Override
    public void checkLastSessionVersion( String features, int currentUpdateVersion ) {
        mPresenter.checkLastSessionVersion( this.activity, features, currentUpdateVersion );
    }

    @Override
    public void showUpdateFeatures( final String features, final OnCompletedListener completedListener ) {

        final HashMap< String, String > stringValuesMap = mPresenter.getStringValues();

        activity.runOnUiThread( new Runnable() {
            @Override
            public void run() {

                new MaterialDialog.Builder( activity )
                        .cancelable( false )
                        .title( stringValuesMap.get( Constants.ANDVERSION_WHATSNEW_TITLE ) )
                        .content( features )
                        .positiveText( stringValuesMap.get( Constants.ANDVERSION_OK ) )
                        .onPositive( new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick( @NonNull MaterialDialog dialog, @NonNull DialogAction which ) {
                                completedListener.onCompleted( "Tamam" );
                            }
                        } )
                        .show();
            }
        } );


    }
}
