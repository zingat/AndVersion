package com.zingat.andversion;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.IOException;

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
        try {
            this.mPresenter.getJsonFromUrl( this.uri, completedListener, new IServerResponseListener() {
                @Override
                public void onParsedData( ParsedContentModel content ) {
                    mPresenter.checkForceUpdate( content );
                }
            } );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

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
        try {
            this.mPresenter.getJsonFromUrl( uri, null, new IServerResponseListener() {
                @Override
                public void onParsedData( ParsedContentModel content ) {
                    mPresenter.checkNewVersionFeatures( content );
                }
            } );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * Make work the helper methods respectively
     * {@link AndVersion#checkForceUpdate(OnCompletedListener)} and {@link AndVersion#checkNews()}
     *
     * @param completedListener Listener that provides to allow to user to resume app after dialog operations.
     * @return Self
     */
    public AndVersion checkUpdate( @Nullable OnCompletedListener completedListener ) {
        try {
            this.mPresenter.getJsonFromUrl( uri, completedListener, new IServerResponseListener() {
                @Override
                public void onParsedData( ParsedContentModel content ) {
                    mPresenter.checkUpdateRules( content );
                }
            } );

        } catch ( IOException e ) {
            e.printStackTrace();
        }

        return this;
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
    public void showForceUpdateDialog( final String whatsNew, final String packageName ) {

        this.mDialog = new MaterialDialog.Builder( activity )
                .cancelable( false )
                .title( R.string.andversion_forceupdate_title )
                .content( whatsNew )
                .positiveText( R.string.andversion_ok )
                .negativeText( R.string.andversion_exit_app )
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

    @Override
    public void showNews( final String features, @Nullable final OnCompletedListener completedListener ) {

        this.mDialog = new MaterialDialog.Builder( this.activity )
                .cancelable( false )
                .title( R.string.andversion_forceupdate_title )
                .content( features )
                .positiveText( R.string.andversion_ok )
                .onPositive( new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick( @NonNull MaterialDialog dialog, @NonNull DialogAction which ) {
                        if ( completedListener != null )
                            completedListener.onCompleted();
                    }
                } )
                .show();
    }

    @Override
    public void showNews( final String features ) {
        this.showNews( features, null );

    }
}
