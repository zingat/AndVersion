package com.zingat.andversionexample;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by mustafaolkun on 08/12/2017.
 *
 * @since 1.1.0
 */
public class AndversionApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults( this );
    }
}
