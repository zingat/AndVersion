package com.zingat.andversion;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * Created by ismailgungor on 28.09.2017.
 */
@Module
public class AndVersionModule {

    @Provides
    OkHttpClient providesOkHttpClient() {
        return new OkHttpClient();
    }
}
