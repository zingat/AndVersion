package com.zingat.andversion;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.zingat.andversion.constants.Constants;

import java.io.IOException;
import java.util.HashMap;

public class AndVersion implements AndVersionContract.View {

    private Activity activity;
    private String uri;
    private MaterialDialog mDialog;

    private static AndVersion andVersion;
    private AndVersionPresenter mPresenter;

    public static AndVersion getInstance() {
        if ( andVersion == null ) {
            andVersion = new AndVersion();
        }

        return andVersion;
    }

    public AndVersion setActivity( Activity activity ) {
        this.activity = activity;
        this.init();

        return this;
    }

    private void init() {
        this.mPresenter.setActivity( this.activity );
        this.mPresenter.setPackageInfoForPresenter();
        this.mPresenter.setView( this );

    }

    public AndVersion setUri( String uri ) {
        this.uri = uri;

        return this;
    }

    private AndVersion() {
        this.mPresenter = new AndVersionPresenter();

    }

    /**
     * Uses to check force update protocol by comparing the app's version and lower version
     * that is defined on json file.
     *
     * @param completedListener Listener that provides to allow to user to resume app after dialog operations.
     * @return Self
     */
    public AndVersion checkForceUpdate( OnCompletedListener completedListener ) {
        this.mPresenter.getForceUpdateInfoFromUrl( uri, completedListener );

        return this;
    }

    /**
     * Uses to check whether a news detail or not to show on dialog.
     * Decides by comparing the app's current version and {@link AndVersionPresenter#lastSessionVersion}
     * If {@link AndVersionPresenter#lastSessionVersion} islower than current version shows a dialog that contains
     * what is news in this version dialog.
     *
     * @return Self
     */
    public AndVersion checkNews() {
        this.mPresenter.getVersionInfoFromUrl( uri );

        return this;
    }

    /**
     * Make work the helper methods respectively
     * {@link AndVersion#checkForceUpdate(OnCompletedListener)} and {@link AndVersion#checkNews()}
     *
     * @param completedListener Listener that provides to allow to user to resume app after dialog operations.
     */
    public void checkUpdate( @Nullable OnCompletedListener completedListener ) {
        try {
            this.mPresenter.getJsonFromUrl( uri, completedListener );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

    }

    /**
     * Closes the dialog if it shows on screen.
     */
    public void closeDialog() {
        if ( this.mDialog != null ) {
            this.mDialog.dismiss();
        }
    }

    @Override
    public void showForceUpdateDialogs( final String whatsNew, final String packageName ) {

        final HashMap< String, String > stringValuesMap = mPresenter.getStringValues();

        activity.runOnUiThread( new Runnable() {
            @Override
            public void run() {

                mDialog = new MaterialDialog.Builder( activity )
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
    public void checkNewsLastSessionVersion( String features, int currentUdateVersion ) {
        mPresenter.checkNewsLastSessionVersion( features, currentUdateVersion );

    }

    @Override
    public void showUpdateFeatures( final String features, final OnCompletedListener completedListener ) {

        final HashMap< String, String > stringValuesMap = mPresenter.getStringValues();

        activity.runOnUiThread( new Runnable() {
            @Override
            public void run() {

                mDialog = new MaterialDialog.Builder( activity )
                        .cancelable( false )
                        .title( stringValuesMap.get( Constants.ANDVERSION_WHATSNEW_TITLE ) )
                        .content( features )
                        .positiveText( stringValuesMap.get( Constants.ANDVERSION_OK ) )
                        .onPositive( new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick( @NonNull MaterialDialog dialog, @NonNull DialogAction which ) {
                                completedListener.onCompleted();
                            }
                        } )
                        .show();
            }
        } );

    }

    @Override
    public void showNews( final String features ) {

        final HashMap< String, String > stringValuesMap = mPresenter.getStringValues();
        mDialog = new MaterialDialog.Builder( activity )
                .cancelable( false )
                .title( stringValuesMap.get( Constants.ANDVERSION_WHATSNEW_TITLE ) )
                .content( features )
                .positiveText( stringValuesMap.get( Constants.ANDVERSION_OK ) )
                .show();
    }
}
