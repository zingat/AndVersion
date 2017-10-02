package com.zingat.andversion;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;


@Module
public class AndVersionModule {

    @Provides
    OkHttpClient providesOkHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    JsonParseHelper provideJsonParseHelper() {
        return new JsonParseHelper();
    }
}
